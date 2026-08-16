# Notas de trabajo

Última actualización: 2026-08-16

---

## Dónde quedamos

El esqueleto del proyecto está **completo y commiteado** (`6f757f1` en `main`, sin push).
`mvn clean test` pasa en verde. **El conflicto de puertos está resuelto**: el proyecto usa
ahora el **5435** y la aplicación arranca y sirve peticiones.

| Cosa | Estado |
|---|---|
| Estructura hexagonal (domain / application / infrastructure) | ✅ completa, con TODOs |
| `mvn clean test` | ✅ BUILD SUCCESS — 13 tests, 13 skipped (`@Disabled`) |
| Documentación (`docs/architecture.md`, ADR 0001, ADR 0002) | ✅ escrita |
| Contexto para agentes (`CLAUDE.md`, `AGENTS.md`) | ✅ escrito |
| Arranque de la aplicación (`mvn spring-boot:run`) | ✅ verificado en 5435 — ver abajo |
| Lógica de negocio | ⬜ sin implementar, a propósito |

---

## 2026-08-16 — Skill `nueva-entidad-dominio`, `Liquidacion` y failsafe

### Las skills nuevas no se pueden invocar hasta reiniciar la sesión

El registro de skills se carga **al arrancar** Claude Code. Una skill recién creada en
`.claude/skills/` existe en disco pero el tool `Skill` responde `Unknown skill` en la misma
sesión en que se escribió; hay que **reiniciar Claude Code** para poder invocarla como
`/nueva-entidad-dominio`. Mientras tanto sigue siendo utilizable a mano: el `SKILL.md` es
un procedimiento legible, no un binario.

Pasó exactamente eso al estrenar `nueva-entidad-dominio`: se creó y se usó en la misma
sesión, siguiendo el fichero paso a paso en lugar del tool.

### `Liquidacion` existe, pero **no cuenta todavía en los balances**

Añadida la entidad completa (dominio → puerto → JPA → mapper → repositorio → adaptador →
`...IT`), toda con `TODO`, más `LiquidacionInvalidaException` y su handler 422.

⚠️ **`CalculadoraBalances` sigue recorriendo solo los gastos.** Un pago ya realizado entre
dos miembros no reduce la deuda que se muestra, así que hoy el balance neto está incompleto
por diseño. Incorporarlo cambia la firma de `CalculadoraBalances.calcular(...)` para que
reciba también las liquidaciones del grupo, y obliga a revisar el test de propiedad
"la suma de saldos es exactamente cero" —que debe seguir cumpliéndose *después* de restar
los pagos—. Pendiente para cuando toque implementar esa lógica: es la **Fase 4** de la
planificación, que en la lista *Pendiente: el trabajo real* de más abajo es el **punto 3**
(`CalculadoraBalances`).

### Ningún test `*IT` se había ejecutado nunca — resuelto en este commit

El `pom.xml` no declaraba `maven-failsafe-plugin`, y los *includes* por defecto de surefire
(`*Test`, `Test*`, `*Tests`, `*TestCase`) **no casan con el sufijo `IT`**. Consecuencia: la
convención `...IT` estaba documentada y usada, pero esas clases no las ejecutaba ningún
comando de Maven. No fallaban: simplemente no existían para el build.

Ya está cableado el reparto estándar — surefire en `test` (unitarios), failsafe en `verify`
(integración). Comprobado con `mvn -o verify`:

```
--- surefire:3.5.3:test ---     Tests run: 13, Skipped: 13
--- failsafe:3.5.3:integration-test ---
Running ...LiquidacionRepositoryAdapterIT
                                Tests run: 4, Skipped: 4
--- failsafe:3.5.3:verify ---   BUILD SUCCESS
```

Los 4 aparecen como *skipped* explícitos por el `@Disabled` de clase, no ignorados. Ese
`@Disabled` es intencionado: sin él, Testcontainers intentaría levantar Docker en cada
`mvn verify`.

> Aviso para la próxima vez: `mvn -o verify` falla en un repo local frío, pero **no por los
> tests** — revienta antes, en `package`, porque `maven-jar-plugin` y sus dependencias nunca
> se habían descargado (`mvn test` no llega a esa fase). Un `mvn verify` online una sola vez
> lo arregla y a partir de ahí el offline funciona.

---

## RESUELTO — puerto final: 5435

Aplicado el 2026-08-11. Comprobado que **5434-5440 estaban todos libres**, se eligió el
**5435** (el que ya proponía el plan).

Cambios:

- `docker-compose.yml`: mapeo `5433:5432` → **`5435:5432`**
- `src/main/resources/application.yml`: datasource → `jdbc:postgresql://localhost:5435/gastos_compartidos`
- `src/test/resources/application-test.yml`: **sin tocar** (Testcontainers, puerto aleatorio)

Antes de aplicarlo, el 5433 tenía **dos** listeners simultáneos, que es exactamente el
síntoma del conflicto:

```
5433 -> com.docker.backend (PID 38712)
5433 -> postgres           (PID 6720)   <- el nativo, el que ganaba
```

Tras `docker compose down && docker compose up -d`, en el 5435 solo escucha Docker
(`wslrelay` + `com.docker.backend`, sin `postgres` nativo), el contenedor queda `healthy`
con `0.0.0.0:5435->5432/tcp`, y el host responde desde el contenedor correcto:

```
gastos_user|16.14      # 16.x = contenedor. Si saliera 12.x, seguiría contestando el nativo
```

> Nota: `psql` no está en el PATH del host. Para comprobarlo desde fuera del contenedor:
> ```powershell
> docker run --rm postgres:16-alpine psql "postgresql://gastos_user:gastos_pass@host.docker.internal:5435/gastos_compartidos" -tAc "select current_user, split_part(version(),' ',2)"
> ```

### Arranque verificado (no por exit code, por log)

`mvn spring-boot:run` — líneas relevantes:

```
HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@1982f6e4
HikariPool-1 - Start completed.
HHH10001005: Database info: ... Database version: 16.14
Initialized JPA EntityManagerFactory for persistence unit 'default'
Tomcat started on port 8080 (http) with context path '/'
Started GastosCompartidosApplication in 5.811 seconds (process running for 6.298)
```

Sin `SQLState: 28P01`, sin excepciones de conexión. Hibernate creó el esquema
(`usuarios`, `grupos`, `grupo_miembros`, `gastos`, `divisiones_gasto`) con `ddl-auto: update`.
Además `GET http://localhost:8080/api/grupos` devuelve **401**, que es lo esperado: Tomcat
sirve y la cadena de seguridad está activa.

El único WARN del log es inofensivo, de la primera creación del esquema:
`constraint "ukkfsp0s1tflm1cwlj8idhqsad0" of relation "usuarios" does not exist, skipping`.

> ⚠️ **El exit code sigue sin servir, y ahora miente en las dos direcciones.** Ya estaba
> anotado que `mvn spring-boot:run` da `BUILD SUCCESS` aunque el `ApplicationContext` falle
> (con DevTools el error ocurre en el hilo `restartedMain` y Maven no lo propaga). Al parar
> la app a mano pasa lo contrario: sale `[ERROR] Process terminated with exit code: -1` y
> `BUILD FAILURE` **después** de un arranque perfectamente correcto. Leer el log, siempre.

---

## El problema original: conflicto en el puerto 5433

*(Se conserva el diagnóstico: el conflicto sigue existiendo en la máquina, simplemente este
proyecto ya no se cruza con él. El PG14 nativo en 5432 sigue siendo un riesgo latente para
otro proyecto.)*

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

## Por qué se movió el proyecto y no los servicios nativos

Se optó por mover **el proyecto**: no requiere permisos de administrador, es reversible y no
rompe otros proyectos que puedan depender de esas instalaciones.

### Alternativas descartadas

| Opción | Coste |
|---|---|
| Parar `postgresql-x64-12` y ponerlo en arranque Manual | Requiere admin; puede romper otros proyectos que lo usen |
| Cambiar el puerto del PG12 nativo en su `postgresql.conf` | Requiere admin + reiniciar el servicio |

---

## Pendiente: el trabajo real

Con la aplicación ya arrancando, toca implementar los TODOs, en este orden sugerido
(de dentro hacia fuera del hexágono):

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
