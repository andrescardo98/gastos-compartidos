---
name: nueva-entidad-dominio
description: Procedimiento completo para anadir un concepto nuevo al dominio de gastos-compartidos, desde la clase de dominio hasta el test de integracion con Testcontainers. Usar cuando se pida crear una entidad, agregado o concepto persistente nuevo (p.ej. "anade Liquidacion", "necesito persistir Categoria"), o cuando haya dudas sobre que ficheros tocar y en que orden para que no se rompa la regla de dependencia.
---

# Nueva entidad de dominio

Crear un concepto nuevo aquí son **8 ficheros en 3 capas**, siempre en el mismo orden:
de dentro hacia fuera. Ese orden no es estético — es la única forma de que el compilador
detecte una inversión de dependencias rota, porque cada paso solo puede apoyarse en los
anteriores.

```
domain/ ──► application/port/out ──► infrastructure/persistence ──► test IT
 (1)(2)            (3)                    (4)(5)(6)(7)                (8)
```

## Antes de empezar

1. Lee la sección 6 de [`docs/architecture.md`](../../../docs/architecture.md)
   ("Dónde va cada cosa"). Si lo que quieres añadir no es un concepto persistente con
   identidad propia, **probablemente no necesitas esta skill**:
   - ¿Es un valor sin identidad (importe, porcentaje, saldo)? → un `record` en
     `domain/model/`, y se acabó. Referencia: `domain/model/Dinero.java`.
   - ¿Es parte de otro agregado y no existe fuera de él? → no lleva puerto ni repositorio
     propios; va dentro del agregado padre. Referencia: `DivisionGasto` vive dentro de
     `Gasto` y su `DivisionGastoJpaEntity` cuelga del `GastoJpaEntity` con
     `cascade = ALL, orphanRemoval = true`.
   - ¿Es una regla que cruza agregados? → `domain/service/`, instanciable con `new`,
     nunca un `@Service`.
2. **Los ficheros nuevos se crean con `TODO`, no implementados** — salvo que se pida lo
   contrario de forma explícita. El esqueleto de este repo está vacío a propósito
   (CLAUDE.md §7). El patrón del repo es javadoc que explica el porqué + cuerpo
   `throw new UnsupportedOperationException("TODO: implementar")`.
3. **Todo el Java va en ASCII**: sin tildes ni `ñ`, tampoco en javadoc, comentarios ni
   strings ("Implementacion", "logica", "division"). Los `.md` sí llevan ortografía completa.
4. Nombres de métodos y variables en castellano: `buscarPorId`, `guardar`, `importeTotal`.

En los ejemplos de abajo el concepto nuevo se llama `Liquidacion` (un pago ya realizado
entre dos miembros). Sustituye por el tuyo.

## Tabla de ficheros

| # | Fichero | Patrón de nombre | Referencia real en el repo |
|---|---|---|---|
| 1 | `domain/model/` | `<Agregado>` | [`Usuario.java`](../../../src/main/java/com/gastoscompartidos/domain/model/Usuario.java), [`Gasto.java`](../../../src/main/java/com/gastoscompartidos/domain/model/Gasto.java) |
| 2 | `domain/exception/` | `...Exception extends DomainException` | [`DivisionInvalidaException.java`](../../../src/main/java/com/gastoscompartidos/domain/exception/DivisionInvalidaException.java) |
| 3 | `application/port/out/` | `<Agregado>RepositoryPort` | [`UsuarioRepositoryPort.java`](../../../src/main/java/com/gastoscompartidos/application/port/out/UsuarioRepositoryPort.java) |
| 4 | `infrastructure/persistence/entity/` | `<Agregado>JpaEntity` | [`UsuarioJpaEntity.java`](../../../src/main/java/com/gastoscompartidos/infrastructure/persistence/entity/UsuarioJpaEntity.java) |
| 5 | `infrastructure/persistence/mapper/` | `<Agregado>Mapper` | [`UsuarioMapper.java`](../../../src/main/java/com/gastoscompartidos/infrastructure/persistence/mapper/UsuarioMapper.java) |
| 6 | `infrastructure/persistence/repository/` | `<Agregado>JpaRepository` | [`UsuarioJpaRepository.java`](../../../src/main/java/com/gastoscompartidos/infrastructure/persistence/repository/UsuarioJpaRepository.java) |
| 7 | `infrastructure/persistence/adapter/` | `<Agregado>RepositoryAdapter` | [`UsuarioRepositoryAdapter.java`](../../../src/main/java/com/gastoscompartidos/infrastructure/persistence/adapter/UsuarioRepositoryAdapter.java) |
| 8 | `src/test/.../infrastructure/` | `<Agregado>RepositoryAdapterIT` | base: [`AbstractIntegrationTest.java`](../../../src/test/java/com/gastoscompartidos/infrastructure/AbstractIntegrationTest.java) |

Esta skill **no cubre** el caso de uso ni el endpoint (`port/in`, `usecase`, `rest/`).
Eso es un trabajo aparte: una entidad puede existir y persistirse antes de que ningún caso
de uso la exponga.

---

## 1. Clase de dominio — `domain/model/<Agregado>.java`

Clase, **no record** (los records son para value objects). Java plano: los únicos `import`
permitidos empiezan por `java.` o `com.gastoscompartidos.domain`.

Reglas que se olvidan, todas visibles en `Usuario.java` y `Gasto.java`:

- **Invariantes en el constructor**, lanzando `DomainException` o una subclase. Un objeto
  que no puede existir no se construye.
- **Identidad por `id`** en `equals`/`hashCode` — nunca por valor. `Usuario.java:66-80`.
- Getters a mano, sin Lombok. Lombok está prohibido en `domain/` aunque desaparezca al
  compilar: es un procesador externo y ataría el dominio a una herramienta.
- Campos que no cambian, `final`. Los que sí, con método de negocio que revalida
  (`Usuario.renombrar`, no `setNombre`).
- **Colecciones: copia defensiva al entrar, `unmodifiableList` al salir**
  (`Gasto.java:50` y `Gasto.java:118-121`). Un agregado que devuelve su lista mutable no
  puede defender su invariante.
- Factories estáticas para el alta, que generan el `id` (`Usuario.registrar`,
  `Gasto.crearEquitativo`).
- Dinero con `BigDecimal` vía `Dinero`, nunca `double`, y con redondeo explícito.

```java
public class Liquidacion {

    private final UUID id;
    private final UUID grupoId;
    private final UUID pagadorId;
    private final UUID receptorId;
    private Dinero importe;
    private LocalDateTime fecha;

    public Liquidacion(UUID id, UUID grupoId, UUID pagadorId, UUID receptorId,
                       Dinero importe, LocalDateTime fecha) {
        // TODO: validar invariantes y lanzar DomainException si no se cumplen:
        //       - ids y fecha no nulos
        //       - pagador y receptor distintos
        //       - importe positivo
        this.id = id;
        // ...
    }

    // TODO: factory de alta que genera el id
    public static Liquidacion registrar(...) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    // getters a mano + equals/hashCode por id
}
```

## 2. Excepciones de dominio — `domain/exception/<Motivo>Exception.java`

Solo si la entidad tiene un modo de fallo con nombre propio. Si basta con
`DomainException("mensaje")`, no inventes una clase.

- Siempre `extends DomainException` (que es unchecked a propósito, ver
  `DomainException.java:5-10`).
- **La excepción no sabe nada de HTTP.** El código de estado lo decide
  `infrastructure/rest/advice/GlobalExceptionHandler.java`. Si creas una excepción nueva,
  **añade allí su `@ExceptionHandler`** siguiendo los códigos ya documentados al final de
  ese fichero: 404 no encontrado, 422 regla de negocio violada, 400 invariante genérica.
- El javadoc dice a qué código se traduce, como en `DivisionInvalidaException.java:6-8`.

## 3. Puerto de salida — `application/port/out/<Agregado>RepositoryPort.java`

Interfaz en `application`, implementada en `infrastructure`. Aquí es donde se invierte la
dependencia, y donde más fácil es romperla sin darse cuenta.

- **Solo tipos de dominio y `java.*` en las firmas.** Nada de `Page`, `Pageable`, `Sort`,
  `Example` ni `<Agregado>JpaEntity`. Si necesitas paginar, define tú el tipo en `domain/`.
  El aviso está escrito en `UsuarioRepositoryPort.java:17-18`.
- Métodos en castellano: `buscarPorId`, `buscarPorGrupo`, `guardar`, `eliminar`,
  `existePorEmail`.
- `Optional<T>` para el uno-o-ninguno, `List<T>` para colecciones.
- **Si el agregado tiene hijos, documenta el N+1 en el javadoc del método de lectura**, como
  hace `GastoRepositoryPort.java:19-25`: el adaptador tendrá que traerlos con `JOIN FETCH` o
  `@EntityGraph`, y ese requisito se pierde si no queda escrito en el puerto.

```java
public interface LiquidacionRepositoryPort {

    // TODO: implementar en el adaptador
    Optional<Liquidacion> buscarPorId(UUID id);

    // TODO: liquidaciones de un grupo, mas recientes primero
    List<Liquidacion> buscarPorGrupo(UUID grupoId);

    // TODO: alta y actualizacion (upsert por id)
    Liquidacion guardar(Liquidacion liquidacion);
}
```

## 4. Entidad JPA — `infrastructure/persistence/entity/<Agregado>JpaEntity.java`

Objeto anémico deliberado: getters/setters y nada más. Duplicar los campos del dominio es
el precio de que el dominio no dependa de JPA (`UsuarioJpaEntity.java:14-22`).

- Aquí **sí** se usa Lombok: `@Getter @Setter @NoArgsConstructor @AllArgsConstructor`.
- `@Table(name = "...")` en plural y snake_case: `usuarios`, `gastos`.
- `@Column` explícito en todos los campos, con `nullable` y `length`.
- **Dinero: `precision = 19, scale = 2`** y el código de moneda en su propia columna
  (`GastoJpaEntity.java:52-57`). Un `Dinero` del dominio se aplana en dos columnas.
- **Enums siempre `@Enumerated(EnumType.STRING)`.** Con `ORDINAL`, reordenar el enum corrompe
  los datos ya guardados en silencio (`GastoJpaEntity.java:28-30`).
- Hijos del agregado: `cascade = ALL, orphanRemoval = true, fetch = LAZY` +
  `@JoinColumn` (`GastoJpaEntity.java:63-73`). Referencias a **otros** agregados van como
  `UUID` plano, no como `@ManyToOne` — igual que `grupoId` y `pagadorId` en `GastoJpaEntity`.
- **Esquema**: hoy lo genera `ddl-auto: update` (`application.yml:15`), así que no hay
  migración que escribir. Está anotado como pendiente de ADR en `docs/architecture.md` §7;
  cuando entre Flyway, este paso crecerá con un fichero de migración.

## 5. Mapper — `infrastructure/persistence/mapper/<Agregado>Mapper.java`

`@Component` con exactamente dos métodos: `aDominio(entity)` y `aEntidad(dominio)`.

- **A mano, sin MapStruct.** Es coste aceptado, no un descuido pendiente de optimizar
  (`UsuarioMapper.java:7-13`).
- `aDominio` reconstruye por el **constructor** del dominio, que revalida invariantes. Si
  hay datos corruptos en BD, el mapeo revienta al leer, y eso es lo correcto: mejor fallar
  que devolver balances erróneos (`GastoMapper.java:20-22`).
- `aDominio` recompone los value objects aplanados: `(importe, codigoMoneda)` → `Dinero`.
- **En actualizaciones no reemplaces la colección de hijos por una instancia nueva**:
  Hibernate rastrea la original. `clear()` + `addAll()` para que `orphanRemoval` funcione
  (`GastoMapper.java:29-34`).
- Los hijos del agregado **no llevan mapper propio**: se mapean dentro del mapper del padre,
  descartando su PK técnica.

## 6. Repositorio Spring Data — `infrastructure/persistence/repository/<Agregado>JpaRepository.java`

`interface ... extends JpaRepository<<Agregado>JpaEntity, UUID>`.

- **Esto NO es el puerto**, y conviene decirlo en el javadoc como hace
  `UsuarioJpaRepository.java:9-13`. Nunca se inyecta en un controller ni en un caso de uso.
- Solo derived queries y `@Query`; habla en tipos JPA (`Optional<UsuarioJpaEntity>`), que
  es precisamente lo que el puerto no puede hacer.
- Si el agregado tiene hijos, aquí va el `@EntityGraph(attributePaths = "...")` o el
  `JOIN FETCH` que evita el N+1 prometido en el puerto.

## 7. Adaptador — `infrastructure/persistence/adapter/<Agregado>RepositoryAdapter.java`

`@Component` que implementa el puerto. Es la única clase que ve a la vez el repositorio
Spring Data y el mapper.

- Dependencias por **constructor**, sin `@Autowired` (`UsuarioRepositoryAdapter.java:26-29`).
- Cuerpo de cada método: mapper → repositorio → mapper. Sin lógica de negocio; si aparece un
  `if` de negocio aquí, pertenece al dominio.
- El javadoc explica que aquí se invierte la dependencia
  (`UsuarioRepositoryAdapter.java:13-19`).

```java
@Component
public class LiquidacionRepositoryAdapter implements LiquidacionRepositoryPort {

    private final LiquidacionJpaRepository jpaRepository;
    private final LiquidacionMapper mapper;

    public LiquidacionRepositoryAdapter(LiquidacionJpaRepository jpaRepository,
                                        LiquidacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Liquidacion> buscarPorId(UUID id) {
        // TODO: jpaRepository.findById(id).map(mapper::aDominio)
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
```

## 8. Test de integración — `src/test/.../infrastructure/<Agregado>RepositoryAdapterIT.java`

Sufijo `IT`, y **hereda de `AbstractIntegrationTest`**, que ya levanta PostgreSQL 16 con
Testcontainers y `@ServiceConnection`. No añadas `@SpringBootTest`, `@Testcontainers` ni
`@DynamicPropertySource`: ya están en la base, y el contenedor es `static` + `withReuse`
para compartirse en toda la suite.

- Solo heredan de aquí los adaptadores. Si un test de dominio o de caso de uso necesita esta
  clase, algo se ha filtrado fuera del hexágono (`AbstractIntegrationTest.java:12-15`).
- El test mínimo es **ida y vuelta**: guardar un objeto de dominio, releerlo por el puerto y
  comprobar que vuelve igual. Se inyecta el **puerto**, no el `JpaRepository`.
- Comprobaciones que este dominio necesita de verdad, y que el TODO de
  `AbstractIntegrationTest.java:45-47` ya pide: que los hijos vuelvan completos y que los
  `BigDecimal` **conserven la escala** (`compareTo` vs `equals`: `10.00` y `10.0` son iguales
  en valor pero no en `equals`; decide cuál estás afirmando).
- Requiere Docker. Si no está corriendo, el test falla al arrancar y es esperado.

```java
class LiquidacionRepositoryAdapterIT extends AbstractIntegrationTest {

    @Autowired
    private LiquidacionRepositoryPort puerto;   // el puerto, no el JpaRepository

    @Test
    void guarda_y_relee_conservando_importe_y_moneda() {
        // TODO: guardar una Liquidacion, releerla por buscarPorId y comparar
    }
}
```

---

## Checklist de verificación

Recórrela **entera** antes de dar el trabajo por cerrado. No hay test de ArchUnit que fuerce
nada de esto todavía (pendiente en el ADR 0001 y en `NOTES.md`), así que se sostiene por
revisión manual.

**Regla de dependencia**

- [ ] Todos los `import` de la clase nueva en `domain/` empiezan por `java.` o
      `com.gastoscompartidos.domain`. Verificable de un vistazo:
      ```
      grep -rn "^import" src/main/java/com/gastoscompartidos/domain/ | grep -v "import java\." | grep -v "import com.gastoscompartidos.domain"
      ```
      Cualquier línea de salida es un fallo.
- [ ] Sin Lombok, sin Spring, sin JPA, sin Jackson en `domain/`.
- [ ] Ningún `JpaRepository` inyectado fuera de `infrastructure/persistence/adapter/`.

**Puerto de salida**

- [ ] Ninguna firma del puerto menciona `Page`, `Pageable`, `Sort`, `Example` ni
      `...JpaEntity`. Comprobación rápida:
      ```
      grep -rn "Page\|Pageable\|Sort\|JpaEntity\|springframework" src/main/java/com/gastoscompartidos/application/port/out/
      ```
      Debe salir vacío.
- [ ] El puerto solo devuelve tipos de dominio, `Optional`, `List` y primitivos.
- [ ] Si hay hijos, el javadoc del método de lectura advierte del N+1.

**Mapper completo**

- [ ] Cuenta los campos: los de `<Agregado>` y los de `<Agregado>JpaEntity` se corresponden
      uno a uno, contando que un `Dinero` son **dos** columnas (`importe` + `codigoMoneda`).
- [ ] Los dos sentidos están cubiertos: cada campo aparece en `aDominio` **y** en `aEntidad`.
      Un campo que solo se escribe se pierde silenciosamente al releer — es el bug clásico
      de este paso y ningún compilador lo detecta.
- [ ] Los hijos del agregado se mapean en ambos sentidos, y `aEntidad` hace `clear()` +
      `addAll()` en lugar de asignar una lista nueva.
- [ ] `aDominio` pasa por el constructor del dominio (revalida invariantes), no por setters.

**Convenciones**

- [ ] Los 8 nombres siguen exactamente la tabla de arriba; ninguna variante inventada.
- [ ] Java en ASCII puro, incluidos javadoc y strings.
- [ ] Excepción nueva → `@ExceptionHandler` correspondiente en `GlobalExceptionHandler`.
- [ ] Finales de línea LF (los fuerza `.gitattributes`; no los toques).
- [ ] Los `TODO` describen pasos concretos y no queda ninguno a medias.

**Compilación**

- [ ] `mvn -q compile` pasa. Ojo: que compile no prueba que la app arranque — para eso, lee
      el log según CLAUDE.md §5, no el exit code.
