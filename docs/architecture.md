# Arquitectura

Este documento describe cómo está organizado el código de **gastos-compartidos** y, sobre todo,
**qué puede depender de qué**. Es la referencia a consultar antes de crear una clase nueva y no
saber en qué paquete va.

---

## 1. Las tres capas

El proyecto sigue **arquitectura hexagonal** (puertos y adaptadores) con vocabulario de DDD.

### `domain/` — el núcleo

Las reglas que serían ciertas aunque no existiera ni la base de datos ni la API.

| Paquete | Contenido |
|---|---|
| `domain/model/` | Entidades (`Usuario`, `Grupo`, `Gasto`) y value objects (`Dinero`, `DivisionGasto`, `Balance`, `PagoSugerido`) |
| `domain/service/` | `CalculadoraBalances`, `SimplificadorDeudas` |
| `domain/exception/` | `DomainException` y sus subtipos |

**Regla dura: cero dependencias externas.** Ni Spring, ni JPA, ni Jackson, ni Lombok.
Solo `java.*`.

Sobre Lombok en concreto: aunque desaparece en compilación, es un procesador de anotaciones
que ata el código a una herramienta externa y hace que el dominio deje de ser Java plano.
En `domain/` los getters y constructores se escriben a mano. En `infrastructure/` sí se usa,
porque las entidades JPA son objetos anémicos donde el ruido no aporta nada.

**Cómo verificar la regla**: si en `domain/` aparece un `import` que no empieza por `java.`
o `com.gastoscompartidos.domain`, la regla está rota. Un test con ArchUnit puede automatizarlo
más adelante.

Las entidades **no son records**: tienen identidad (`equals`/`hashCode` por `id`) e invariantes
que se validan en el constructor. Los value objects **sí son records**: son inmutables y su
identidad es su valor.

### `application/` — los casos de uso

Orquesta. **No calcula.** Carga agregados por los puertos, delega la regla en el dominio,
persiste el resultado.

| Paquete | Contenido |
|---|---|
| `application/port/in/` | **Interfaces** de los casos de uso + sus Command/Query/Result |
| `application/usecase/` | **Implementaciones** de esas interfaces |
| `application/port/out/` | Interfaces de lo que el núcleo necesita del exterior |

Aquí sí hay anotaciones de Spring (`@Service`, `@Transactional`): esta capa es la que define
el límite transaccional. El dominio sigue limpio.

Si un caso de uso empieza a acumular `if`s de negocio, ese código pertenece al dominio.

### `infrastructure/` — los adaptadores

Todo lo reemplazable: la base de datos, el protocolo, el mecanismo de autenticación.

| Paquete | Contenido |
|---|---|
| `infrastructure/rest/` | Controllers, DTOs, `GlobalExceptionHandler` — **adaptadores de entrada** |
| `infrastructure/persistence/` | Entidades JPA, repositorios Spring Data, mappers, adaptadores — **adaptadores de salida** |
| `infrastructure/security/` | Configuración de Spring Security y JWT |

---

## 2. La regla de dependencia

```
        infrastructure  ──────►  application  ──────►  domain
             (rest)                (usecase)            (model)
          (persistence)             (port)             (service)
           (security)
```

**Las flechas solo apuntan hacia dentro. Nunca hacia fuera.**

| Capa | Puede importar de | Nunca importa de |
|---|---|---|
| `domain` | solo `java.*` | `application`, `infrastructure`, cualquier framework |
| `application` | `domain` | `infrastructure` |
| `infrastructure` | `application`, `domain` | — |

### Puertos de entrada (driving) vs. de salida (driven)

**Puertos de entrada** (`port/in`): lo que el núcleo **ofrece**. Una interfaz por caso de uso.

```java
public interface CrearGastoUseCase {
    GastoResult ejecutar(CrearGastoCommand command);
}
```

Los controllers dependen de **la interfaz**, nunca de `CrearGastoService`. Dos ventajas
concretas: en un `@WebMvcTest` se mockea el caso de uso entero con
`@MockitoBean CrearGastoUseCase` sin tocar dominio ni base de datos, y se puede sustituir la
implementación sin recompilar el controller.

**Puertos de salida** (`port/out`): lo que el núcleo **necesita**. Aquí ocurre la inversión
de dependencias — la interfaz vive en `application`, la implementación en `infrastructure`.

```
   application                      infrastructure
   ───────────                      ──────────────
   GastoRepositoryPort   ◄────────  GastoRepositoryAdapter
      (interfaz)          implementa      (usa Spring Data)
```

En tiempo de ejecución `application` llama a código de `infrastructure`; en tiempo de
compilación la flecha apunta al revés. Eso es lo que permite que el núcleo no sepa que
existe PostgreSQL.

Detalle fácil de olvidar: los puertos de salida **hablan el lenguaje del dominio**. Nada de
`Page`, `Pageable` ni `Optional<UsuarioJpaEntity>` en sus firmas. Si aparece un tipo de
Spring Data en un puerto, la inversión se ha roto.

---

## 3. Flujo de una request

`POST /api/grupos/{grupoId}/gastos` — registrar un gasto:

```
   CLIENTE
     │  HTTP POST + JSON + Bearer token
     ▼
┌─────────────────────────────────────────────────────────────────┐
│ INFRASTRUCTURE / security                                       │
│   JwtAuthenticationFilter → valida token, deja el id en contexto│
└─────────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────────┐
│ INFRASTRUCTURE / rest                                           │
│   GastoController                                               │
│     - @Valid CrearGastoRequest  (formato del JSON)              │
│     - traduce Request ──► CrearGastoCommand                     │
└─────────────────────────────────────────────────────────────────┘
     │  depende de la INTERFAZ CrearGastoUseCase
     ▼
┌─────────────────────────────────────────────────────────────────┐
│ APPLICATION / port.in → usecase                                 │
│   CrearGastoService implements CrearGastoUseCase                │
│     1. grupoRepository.buscarPorId()   ── puerto de salida ──┐  │
│     2. delega la regla en el dominio                         │  │
│     3. gastoRepository.guardar()       ── puerto de salida ──┤  │
│     4. devuelve GastoResult                                  │  │
└──────────────────────────────────────────────────────────────┼──┘
     │                                                         │
     ▼                                                         │
┌────────────────────────────────────┐                         │
│ DOMAIN                             │                         │
│   Gasto.crearEquitativo(...)       │                         │
│     - valida que las partes        │                         │
│       sumen el total   ◄── LA REGLA│                         │
│   (sin Spring, sin JPA, sin HTTP)  │                         │
└────────────────────────────────────┘                         │
                                                               │
                          ┌────────────────────────────────────▼──┐
                          │ INFRASTRUCTURE / persistence          │
                          │   GastoRepositoryAdapter              │
                          │     implements GastoRepositoryPort    │
                          │   GastoMapper: dominio ◄──► JPA       │
                          │   GastoJpaRepository → PostgreSQL     │
                          └───────────────────────────────────────┘
     │
     ▼  GastoResult ──► GastoResponse ──► 201 Created
   CLIENTE
```

Lo importante del diagrama: la caja `DOMAIN` **no tiene ninguna flecha saliente hacia
infraestructura**. Recibe datos, decide, y devuelve. Nunca va a buscar nada.

### El mismo flujo, en modelos de datos

Un mismo gasto cambia de forma cuatro veces, y cada frontera es deliberada:

```
CrearGastoRequest   (rest/dto)          ← anotaciones Jackson + Bean Validation
      ↓
CrearGastoCommand   (application/port/in) ← sin HTTP; invocable desde un test o una cola
      ↓
Gasto               (domain/model)      ← Java puro, con invariantes
      ↓
GastoJpaEntity      (persistence/entity) ← anotaciones JPA
```

Sí, es repetitivo. Ese es el coste explícito de la arquitectura, y está aceptado en el
[ADR 0001](decisions/0001-arquitectura-hexagonal.md). La compensación: cambiar el JSON público
no toca el dominio, y cambiar el esquema de tablas tampoco.

---

## 4. Manejo de errores

El dominio lanza excepciones que **no saben que existe HTTP**:

| Excepción de dominio | HTTP | Cuándo |
|---|---|---|
| `GrupoNoEncontradoException` | 404 | El grupo no existe |
| `UsuarioNoEncontradoException` | 404 | El usuario no existe |
| `DivisionInvalidaException` | 422 | Las partes no suman el total |
| `DomainException` (resto) | 400 | Otra invariante violada |
| `MethodArgumentNotValidException` | 400 | El JSON no pasa Bean Validation |

La traducción ocurre en un único sitio: `infrastructure/rest/advice/GlobalExceptionHandler`.

422 y no 400 para `DivisionInvalidaException` porque el JSON es sintácticamente correcto;
lo que falla es una regla de negocio.

---

## 5. Estrategia de tests

La arquitectura se paga con verbosidad y se cobra aquí:

| Qué se prueba | Cómo | Necesita |
|---|---|---|
| `domain/service`, `domain/model` | JUnit 5 puro | nada |
| `application/usecase` | Mockito sobre los **puertos de salida** | nada |
| `infrastructure/persistence` | Testcontainers (`AbstractIntegrationTest`) | Docker |
| `infrastructure/rest` | `@WebMvcTest` + mock del **puerto de entrada** | nada |

Los tests de reglas de negocio no levantan Spring. **Si algún día un test de negocio
necesita `@SpringBootTest`, es señal de que la regla se ha escapado del dominio** — es el
mejor detector de fugas que da esta arquitectura.

Dos aserciones de propiedad que conviene no perder de vista:
- La suma de todos los balances de un grupo es **exactamente cero**. Caza cualquier céntimo
  perdido al repartir.
- Aplicar los pagos sugeridos a los balances deja **todos los saldos a cero**, con como
  máximo `n-1` transferencias.

---

## 6. Dónde va cada cosa

| Quiero añadir... | Va en... |
|---|---|
| Una regla de negocio nueva | `domain/model` (si es de una entidad) o `domain/service` (si cruza agregados) |
| Un caso de uso nuevo | interfaz en `application/port/in` + implementación en `application/usecase` |
| Un endpoint nuevo | `infrastructure/rest/controller` — **y antes su puerto de entrada** |
| Una consulta a BD nueva | método en el puerto de `port/out` + implementación en el adaptador |
| Un cliente HTTP externo | puerto en `port/out` + adaptador en `infrastructure/` |

El error más común y más fácil de cometer: **inyectar un `JpaRepository` directamente en un
controller o en un caso de uso** para ir rápido. Eso salta el puerto y rompe el hexágono. Si
la tentación aparece, es que falta un puerto.

---

## 7. Decisiones registradas

Las decisiones de arquitectura se documentan como ADRs en [`docs/decisions/`](decisions/).

- [0001 — Adoptar arquitectura hexagonal / DDD](decisions/0001-arquitectura-hexagonal.md)
- [0002 — Mantener la autenticación fuera de `application`](decisions/0002-autenticacion-fuera-de-application.md)

Decisiones aún pendientes de ADR, anotadas para no olvidarlas:
- Heurística greedy en `SimplificadorDeudas` (no garantiza el mínimo absoluto de transferencias).
- Migraciones de esquema: hoy `ddl-auto: update`, insostenible en cuanto haya datos reales.
