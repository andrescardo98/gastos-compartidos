# Notas de trabajo

Última actualización: 2026-08-10

---

## Dónde quedamos

El esqueleto del proyecto está **completo y commiteado** (`6f757f1` en `main`, sin push).
`mvn clean test` pasa en verde. Lo que **no** arranca es la aplicación: falla al conectar
con PostgreSQL por un conflicto de puertos en la máquina de desarrollo.

| Cosa | Estado |
|---|---|
| Estructura hexagonal (domain / application / infrastructure) | ✅ completa, con TODOs |
| `mvn clean test` | ✅ BUILD SUCCESS — 13 tests, 13 skipped (`@Disabled`) |
| Documentación (`docs/architecture.md`, ADR 0001) | ✅ escrita |
| Arranque de la aplicación (`mvn spring-boot:run`) | ❌ falla — ver abajo |
| Lógica de negocio | ⬜ sin implementar, a propósito |

---

## El problema: conflicto en el puerto 5433

**Dos servidores PostgreSQL se disputan el 5433, y gana el equivocado.**

En esta máquina hay dos instalaciones **nativas** de PostgreSQL como servicios de Windows,
ambas con arranque **automático** — es decir, levantan antes que Docker:

| Servicio | Puerto configurado | Choca con |
|---|---|---|
| `postgresql-x64-12` | **5433** | ⚠️ el contenedor `gastos-compartidos-db` de este proyecto |
| `postgresql-x64-14` | 5432 | el contenedor `backend-db-1` de otro proyecto (riesgo latente) |

El contenedor `gastos-compartidos-db` declara el mapeo `0.0.0.0:5433->5432` y aparece como
`healthy`, pero **el listener real del host en 5433 es el `postgres.exe` nativo**, porque ya
tenía el puerto tomado cuando Docker intentó publicarlo.

Resultado: cuando la app abre `jdbc:postgresql://localhost:5433/gastos_compartidos`, habla con
**PostgreSQL 12 nativo**, que no tiene el rol `gastos_user`:

```
SQL Error: 0, SQLState: 28P01
FATAL: password authentication failed for user "gastos_user"
```

### Por qué el síntoma engaña

Un error de autenticación hace pensar en credenciales mal puestas. **No lo son.**
`application.yml` y `docker-compose.yml` están correctos. Lo que falla es *a qué servidor*
llega la conexión. Prueba concluyente:

```bash
# DENTRO del contenedor: funciona
docker exec gastos-compartidos-db psql -U gastos_user -d gastos_compartidos -tAc "select current_user, split_part(version(),' ',2)"
# -> gastos_user|16.14

# Contra el 5433 del HOST: falla
psql "postgresql://gastos_user:gastos_pass@localhost:5433/gastos_compartidos" -tAc "select 1"
# -> FATAL: password authentication failed for user "gastos_user"
```

### Cómo confirmarlo en el futuro

El PID cambia en cada reinicio (en el diagnóstico original era el 6720), así que lo que
importa es **el nombre del proceso**, no el número:

```powershell
Get-NetTCPConnection -LocalPort 5433 -State Listen | ForEach-Object {
  $p = Get-Process -Id $_.OwningProcess
  "PID $($_.OwningProcess) -> $($p.ProcessName)"
}
```

Si el proceso se llama **`postgres`**, es la instalación nativa secuestrando el puerto.
Si fuese Docker, aparecería un proceso de Docker Desktop.

---

## Próximo paso: mover este proyecto a un puerto libre

Se elige mover **el proyecto**, no tocar los servicios nativos: no requiere permisos de
administrador, es reversible y no rompe otros proyectos que puedan depender de esas
instalaciones.

Puerto propuesto: **5435**.

> ⚠️ **Pendiente de verificar antes de aplicar**: que el 5435 esté realmente libre.
> La comprobación no llegó a ejecutarse.
> ```powershell
> Get-NetTCPConnection -LocalPort 5435 -State Listen -ErrorAction SilentlyContinue
> ```
> Sin salida = libre. Si estuviera ocupado, probar 5436, 5437…

### 1. `docker-compose.yml`

```diff
     ports:
-      - "5433:5432"
+      - "5435:5432"
```

### 2. `src/main/resources/application.yml`

```diff
   datasource:
-    url: jdbc:postgresql://localhost:5433/gastos_compartidos
+    url: jdbc:postgresql://localhost:5435/gastos_compartidos
```

### 3. Recrear el contenedor y verificar

```bash
docker compose down
docker compose up -d
docker compose ps          # debe salir healthy con 0.0.0.0:5435->5432

# comprobar que ahora SI responde el contenedor y no el nativo
psql "postgresql://gastos_user:gastos_pass@localhost:5435/gastos_compartidos" -tAc "select split_part(version(),' ',2)"
# esperado: 16.x  (si sale 12.x, sigue contestando el nativo)
```

### 4. Levantar la aplicación

```bash
mvn spring-boot:run
```

Buscar en el log: `Started GastosCompartidosApplication in X seconds`.

> ⚠️ **No te fíes del exit code.** `mvn spring-boot:run` terminó con `BUILD SUCCESS` y
> exit 0 aun cuando el `ApplicationContext` falló: con DevTools el error ocurre en el hilo
> `restartedMain` y Maven no lo propaga. Hay que leer el log, no el código de salida.

`src/test/resources/application-test.yml` **no hay que tocarlo**: los tests de integración
usan Testcontainers con `@ServiceConnection`, que asigna un puerto aleatorio y es inmune a
este conflicto.

---

## Alternativas descartadas (por si el cambio de puerto no convence)

| Opción | Coste |
|---|---|
| Parar `postgresql-x64-12` y ponerlo en arranque Manual | Requiere admin; puede romper otros proyectos que lo usen |
| Cambiar el puerto del PG12 nativo en su `postgresql.conf` | Requiere admin + reiniciar el servicio |

---

## Pendiente después de esto

Una vez arranque la aplicación, el trabajo real es implementar los TODOs, en este orden
sugerido (de dentro hacia fuera del hexágono):

1. **`Dinero`** — sobre todo `repartirEn(int)`, que debe repartir los céntimos sobrantes sin
   perder ninguno. Es la base de todo lo demás.
2. **`Gasto`** — las factories y la invariante de que las divisiones suman el total.
3. **`CalculadoraBalances`** — con el test de propiedad "la suma de saldos es exactamente cero".
4. **`SimplificadorDeudas`** — heurística greedy; pendiente de ADR propio.
5. Mappers y adaptadores de persistencia (tests con `AbstractIntegrationTest`).
6. Casos de uso y controllers.
7. `JwtService` y `JwtAuthenticationFilter` — hoy el filtro es un **pass-through** que no
   autentica nada; los endpoints están protegidos solo porque sin autenticación en el
   contexto todo lo que no sea `/api/auth/**` responde 401.

Otros cabos sueltos anotados en `docs/architecture.md`:

- `ddl-auto: update` es insostenible en cuanto haya datos reales → Flyway o Liquibase.
- Falta un test de **ArchUnit** que falle si `domain` importa algo fuera de `java.*`.
- `GrupoController` está sin endpoints a propósito: primero hay que crear sus puertos de
  entrada en `application/port/in`.
