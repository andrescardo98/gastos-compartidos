# AGENTS.md

Instrucciones operativas para cualquier agente que trabaje en **gastos-compartidos**:
comandos, entorno, ficheros protegidos y qué se exige para dar una tarea por terminada.

Las convenciones de arquitectura y estilo están en [`CLAUDE.md`](CLAUDE.md) y, con detalle,
en [`docs/architecture.md`](docs/architecture.md).

## Entorno

| Dato | Valor |
|---|---|
| Java | 21 (`maven.compiler.release=21`; `JAVA_HOME` puede apuntar a un JDK más nuevo) |
| Spring Boot | 3.5.3 |
| Base de datos | PostgreSQL 16 en Docker, contenedor `gastos-compartidos-db` |
| **Puerto de PostgreSQL** | **5435** en el host → 5432 en el contenedor |
| Puerto de la aplicación | 8080 |
| Tests de integración | Testcontainers (puerto aleatorio, **requiere Docker corriendo**) |

### El puerto 5435 no es negociable

El proyecto usó 5433 y no arrancaba. La causa: en esta máquina hay dos PostgreSQL **nativos**
como servicios de Windows con arranque automático, y `postgresql-x64-12` tiene tomado el 5433
antes de que Docker publique el suyo. La conexión llegaba al servidor equivocado y fallaba con
`SQLState: 28P01 / password authentication failed for user "gastos_user"` — un error que
parece de credenciales y no lo es.

Se movió el proyecto al **5435** (comprobado libre, 5434-5440 lo estaban). El servicio nativo
sigue ahí: **no vuelvas al 5433 ni al 5432**. Diagnóstico completo, pruebas y alternativas
descartadas en [`NOTES.md`](NOTES.md).

## Comandos

Todos desde la raíz del repo.

```bash
# 1. Base de datos (siempre lo primero: la app y los tests de integración la necesitan)
docker compose up -d
docker compose ps          # el contenedor debe quedar "healthy"
docker compose logs -f postgres
docker compose down        # para; los datos sobreviven en el volumen gastos_pg_data
docker compose down -v     # ATENCION: -v borra el volumen y los datos. Preguntar antes.

# 2. Tests
mvn clean test

# 3. Arrancar la aplicación
mvn spring-boot:run
```

Si `mvn` no está en el PATH, usa el wrapper del repo: `./mvnw` (o `mvnw.cmd` en PowerShell).

Estado esperado de `mvn clean test` hoy: **BUILD SUCCESS, 13 tests, 13 skipped** — los tests
están escritos y marcados `@Disabled` a la espera de que se implementen los `TODO`. Si el
número cambia, dilo en el informe.

### Comprobar el arranque: por log, nunca por exit code

`mvn spring-boot:run` **no es fiable como señal de éxito o fracaso**: da `BUILD SUCCESS`
aunque el `ApplicationContext` falle (con DevTools el error ocurre en el hilo `restartedMain`
y Maven no lo propaga), y da `BUILD FAILURE` con `Process terminated with exit code: -1`
cuando se para la app a mano tras un arranque correcto. Está documentado en `NOTES.md`.

Arranque correcto = estas líneas en el log:

```
HikariPool-1 - Start completed.
Database version: 16.14
Initialized JPA EntityManagerFactory for persistence unit 'default'
Tomcat started on port 8080 (http) with context path '/'
Started GastosCompartidosApplication in N seconds
```

Y `GET http://localhost:8080/api/grupos` debe devolver **401**: significa que Tomcat sirve y
la cadena de seguridad está activa. Un 200 sería un problema, no un éxito.

Comprobar contra qué servidor se está hablando realmente (`psql` no está en el PATH del host):

```bash
docker run --rm postgres:16-alpine psql \
  "postgresql://gastos_user:gastos_pass@host.docker.internal:5435/gastos_compartidos" \
  -tAc "select current_user, split_part(version(),' ',2)"
```

Debe responder `gastos_user|16.x`. Si sale `12.x`, está contestando el PostgreSQL nativo.

## Ficheros que NO se modifican sin confirmación explícita

Pregunta antes de tocar, aunque el cambio parezca trivial o sea un efecto colateral de otra
tarea:

| Fichero | Por qué |
|---|---|
| `docker-compose.yml` | Contiene el mapeo `5435:5432`. Cambiarlo reabre el conflicto de puertos que costó una sesión entera diagnosticar. |
| `src/main/resources/application.yml` | Contiene la URL del datasource con el 5435. Debe seguir en sincronía con `docker-compose.yml`. |

Regla derivada: `src/test/resources/application-test.yml` **no debe fijar ningún puerto**.
Los tests de integración usan Testcontainers con puerto aleatorio inyectado por
`@ServiceConnection`; apuntarlos al 5435 los ataría a la base de datos de desarrollo.

Si una tarea exige de verdad cambiar uno de estos ficheros, pide confirmación explicando qué
línea y por qué, y actualiza `NOTES.md` con el cambio.

## Checklist antes de dar una tarea por terminada

1. **`mvn clean test` pasa.** Ejecutado, no supuesto. Pega el resumen real
   (`Tests run: N, Failures: 0, Errors: 0, Skipped: M`). Si algo falla o se salta, dilo con
   la salida; no lo redondees a "todo bien".
2. **Si hubo una decisión de arquitectura**: hay un ADR nuevo en `docs/decisions/` con el
   formato Título / Estado / Contexto / Decisión / Consecuencias, con sus condiciones de
   reversión si la decisión es contingente, y la sección 7 de `docs/architecture.md` está
   actualizada (añadida a "Decisiones registradas" y quitada de las pendientes).
3. **`NOTES.md` refleja el estado real**: fecha de última actualización, tabla "Dónde
   quedamos" y la lista de pendientes. Si resolviste algo que estaba anotado como pendiente,
   quítalo; si descubriste algo nuevo, anótalo. `NOTES.md` es la memoria del proyecto entre
   sesiones y un `NOTES.md` desfasado hace perder más tiempo del que ahorra.
4. **Si tocaste el arranque, la configuración o la persistencia**: arranque verificado por
   log, con las líneas pegadas. El exit code no cuenta.
5. **No se modificó ningún fichero protegido** sin confirmación previa.
6. **Di explícitamente qué quedó fuera** y por qué: `TODO` que siguen abiertos, tests que no
   escribiste, casos de borde que no cubriste. Una tarea entregada con huecos declarados es
   útil; una entregada como completa sin serlo, no.
