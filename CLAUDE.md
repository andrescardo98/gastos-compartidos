# CLAUDE.md

Cómo trabajar en **gastos-compartidos**. Este fichero es sobre *criterio*: qué convenciones
seguir y qué cosas no se dan por buenas sin comprobarlas. Los comandos y las reglas operativas
están en [`AGENTS.md`](AGENTS.md).

## Qué es este proyecto

Aplicación Spring Boot para repartir gastos dentro de un grupo: registrar quién pagó qué,
calcular el saldo neto de cada miembro y sugerir las transferencias que saldan las deudas.

**El objetivo primario no es entregar rápido, es practicar arquitectura hexagonal y DDD.**
Eso cambia el criterio por defecto: cuando haya que elegir entre el atajo pragmático y la
opción que mantiene las fronteras visibles, gana la segunda, y el coste en verbosidad está
aceptado explícitamente en el [ADR 0001](docs/decisions/0001-arquitectura-hexagonal.md).
No propongas simplificaciones que borren fronteras "porque el proyecto es pequeño" — ese
trade-off ya está decidido.

Estado: el esqueleto está completo y la mayoría de los métodos son `TODO` **a propósito**.
Ver `NOTES.md` para el orden de implementación previsto.

## 1. La regla de dependencia

La referencia completa es [`docs/architecture.md`](docs/architecture.md). **Léela antes de
crear una clase nueva**, sobre todo su sección 6 ("Dónde va cada cosa"). El resumen:

```
infrastructure  ──►  application  ──►  domain
```

| Capa | Puede importar de | Nunca importa de |
|---|---|---|
| `domain` | solo `java.*` | `application`, `infrastructure`, cualquier framework |
| `application` | `domain` | `infrastructure` |
| `infrastructure` | `application`, `domain` | — |

Aún **no hay test de ArchUnit** que fuerce esto (está anotado como pendiente en el ADR 0001 y
en `NOTES.md`). Mientras no exista, la regla se sostiene por revisión: si tocas `domain/`,
comprueba a mano que todo `import` empieza por `java.` o `com.gastoscompartidos.domain`.

El error más fácil de cometer, y el que hay que vigilar activamente: **inyectar un
`JpaRepository` directamente en un controller o en un caso de uso** para ir rápido. Eso salta
el puerto. Si aparece la tentación, es que falta un puerto.

## 2. `domain/` es Java plano

Sin Spring, sin JPA, sin Jackson y **sin Lombok**. Lombok está en la prohibición aunque
desaparezca al compilar: es un procesador de anotaciones externo y ata el dominio a una
herramienta. En `domain/` los getters y constructores se escriben a mano; en
`infrastructure/` sí se usa (`@Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor` en las
entidades JPA, que son objetos anémicos).

- **Entidades** (`Usuario`, `Grupo`, `Gasto`): clases, no records. Identidad por `id` en
  `equals`/`hashCode`, invariantes validadas en el constructor.
- **Value objects** (`Dinero`, `DivisionGasto`, `Balance`, `PagoSugerido`): records inmutables.
- **Domain services** (`CalculadoraBalances`, `SimplificadorDeudas`): se instancian con `new`,
  **no son beans**. Convertirlos en `@Service` obligaría al dominio a conocer Spring.
- Aritmética monetaria con `BigDecimal` y redondeo explícito. `double` está descartado.

`application/` sí lleva anotaciones de Spring (`@Service`, `@Transactional`): es la capa que
define el límite transaccional. Pero **orquesta, no calcula**. Si un caso de uso empieza a
acumular `if`s de negocio, ese código pertenece al dominio.

## 3. Convenciones de nombres

Ya están establecidas en el código; síguelas sin inventar variantes.

| Elemento | Patrón | Ejemplo real |
|---|---|---|
| Puerto de entrada (`port/in`) | `<Verbo><Sustantivo>UseCase` | `CrearGastoUseCase` |
| Implementación (`usecase`) | `<Verbo><Sustantivo>Service` | `CrearGastoService` |
| Entrada de un caso de uso que escribe | `...Command` (record) | `CrearGastoCommand` |
| Entrada de un caso de uso que lee | `...Query` (record) | `ObtenerBalanceGrupoQuery` |
| Salida de un caso de uso | `...Result` (record) | `GastoResult`, `BalanceGrupoResult` |
| Puerto de salida (`port/out`) | `<Agregado>RepositoryPort` | `GastoRepositoryPort` |
| Adaptador de persistencia | `<Agregado>RepositoryAdapter` | `GastoRepositoryAdapter` |
| Repositorio Spring Data | `<Agregado>JpaRepository` | `GastoJpaRepository` |
| Entidad JPA | `<Agregado>JpaEntity` | `GastoJpaEntity` |
| Mapper dominio ↔ JPA | `<Agregado>Mapper` | `GastoMapper` |
| DTO REST | `...Request` / `...Response` | `CrearGastoRequest`, `GastoResponse` |
| Excepción de dominio | `...Exception extends DomainException` | `DivisionInvalidaException` |
| Test de integración (Testcontainers) | `...IT` | `GastoRepositoryAdapterIT` |

Detalles que se olvidan:

- **Todos los puertos de entrada exponen un único método `ejecutar(...)`.** Uno por caso de
  uso, no interfaces con varios métodos.
- **Los controllers dependen de la interfaz** (`CrearGastoUseCase`), nunca de
  `CrearGastoService`. Excepción única y documentada: `AuthController`, ver
  [ADR 0002](docs/decisions/0002-autenticacion-fuera-de-application.md).
- **Los puertos de salida hablan el lenguaje del dominio.** Nada de `Page`, `Pageable` ni
  `Optional<UsuarioJpaEntity>` en sus firmas. Si aparece un tipo de Spring Data en un puerto,
  la inversión de dependencias está rota.
- **`JpaEntity` y modelo de dominio nunca se mezclan.** Un gasto cambia de forma cuatro veces
  —`CrearGastoRequest` → `CrearGastoCommand` → `Gasto` → `GastoJpaEntity`— y el mapeo se
  escribe a mano. Es coste aceptado, no un descuido que haya que "optimizar" con MapStruct.
- Métodos y variables en **castellano** (`buscarPorId`, `guardar`, `importeTotal`), igual que
  el lenguaje ubicuo del dominio.

## 4. ADRs

Toda decisión de arquitectura va a `docs/decisions/`, numerada: `NNNN-titulo-en-kebab-case.md`.
Los existentes son 0001 (hexagonal/DDD) y 0002 (autenticación fuera de `application`).

Estructura fija:

```
# NNNN — Título

## Estado
Aceptada — AAAA-MM-DD

## Contexto
## Decisión
## Consecuencias
### Positivas
### Negativas
### Riesgos y mitigaciones   (tabla riesgo | mitigación)

## Alternativas consideradas  (cuando se descartó algo relevante)
```

Qué se espera del contenido, mirando el 0002 como patrón:

- **Las alternativas descartadas se argumentan, no se listan.** Cada una con el motivo
  concreto por el que pierde. Si una alternativa era defendible, dilo.
- **Condiciones de reversión explícitas cuando la decisión sea contingente.** El 0002 lo hace
  en el punto 4 de la Decisión: la excepción vale "mientras autenticar y registrar sean
  puramente técnicos", y nombra qué hecho concreto la invalidaría (que el registro adquiera
  reglas de negocio) y qué habría que hacer entonces (promover a `RegistrarUsuarioUseCase`).
  Incluye también qué **no** cuenta como disparador, para que la condición sea comprobable.
- **La tabla de riesgos lista mitigaciones accionables**, no buenas intenciones.
- Enlaces relativos entre ADRs y hacia `docs/architecture.md`.

Al añadir o cambiar un ADR, actualiza también la sección 7 de `docs/architecture.md`
("Decisiones registradas") — incluida la lista de decisiones *pendientes* de ADR, de la que
hay que sacar la que acabas de registrar.

## 5. Verificar el arranque de verdad

**El exit code de `mvn spring-boot:run` no vale como prueba de nada, y miente en las dos
direcciones:**

- Da `BUILD SUCCESS` aunque el `ApplicationContext` falle — con DevTools el error ocurre en el
  hilo `restartedMain` y Maven no lo propaga.
- Da `BUILD FAILURE` con `Process terminated with exit code: -1` al parar la app a mano,
  después de un arranque perfectamente correcto.

El caso está documentado en `NOTES.md` (fue el conflicto del puerto 5433). **Lee el log.**
Un arranque bueno tiene estas líneas:

```
HikariPool-1 - Start completed.
Database version: 16.14
Initialized JPA EntityManagerFactory for persistence unit 'default'
Tomcat started on port 8080 (http) with context path '/'
Started GastosCompartidosApplication in N seconds
```

Y como comprobación de que además sirve tráfico: `GET http://localhost:8080/api/grupos` debe
devolver **401** (Tomcat responde y la cadena de seguridad está activa). Un 200 aquí sería la
señal de alarma, no un éxito.

Si ves `SQLState: 28P01` / `password authentication failed for user "gastos_user"`, **no son
las credenciales**: es un PostgreSQL nativo de Windows secuestrando el puerto. El diagnóstico
completo y el comando para confirmarlo están en `NOTES.md`.

La misma desconfianza aplica en general: no des por hecho que algo funciona porque el comando
terminó en 0. Comprueba el efecto observable y di qué comprobaste.

## 6. Estilo

- **Código Java: solo ASCII.** Ni tildes ni `ñ`, tampoco en comentarios, javadoc ni literales
  de string ("Implementacion", "logica", "validacion"). Es deliberado, para no depender de la
  codificación del entorno. Los `.md` sí van con ortografía completa.
- **Los comentarios explican el porqué, no el qué.** El estilo del repo son javadocs que
  justifican una decisión o avisan de una trampa (el N+1 en `GastoRepositoryPort`, por qué
  `@ServiceConnection` sustituye al `@DynamicPropertySource`). No añadas comentarios que
  repitan la firma.
- **Los `TODO` describen el trabajo pendiente en pasos concretos**, como en `CrearGastoService`.
  Si implementas uno, borra el `TODO` entero; no lo dejes a medias.
- Finales de línea **LF**, forzados por `.gitattributes`. No los cambies.
- Mensajes de commit en castellano con prefijo tipo Conventional Commits (`feat:`, `fix:`,
  `docs:`, `chore:`), como el historial existente.

## 7. Cómo responder aquí

- **No implementes la lógica de negocio salvo que te lo pida.** Los `TODO` están vacíos a
  propósito: el proyecto es una práctica y resolverlos por mi cuenta le quita el sentido.
  Si te pido diseño o revisión, quédate en diseño o revisión.
- Si algo contradice `docs/architecture.md` o un ADR, dilo antes de escribir código.
- Si una decisión merece ADR, propónlo en vez de tomarla por tu cuenta en un commit.
