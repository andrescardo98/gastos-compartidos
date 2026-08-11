# 0001 — Adoptar arquitectura hexagonal con vocabulario DDD

## Estado

Aceptada — 2026-08-10

## Contexto

`gastos-compartidos` es una aplicación para repartir gastos dentro de un grupo de personas:
registrar quién pagó qué, calcular el saldo neto de cada miembro y sugerir el conjunto mínimo
de transferencias que salda las deudas.

Su objetivo primario **no es llegar a producción, sino servir de práctica deliberada de
arquitectura hexagonal y DDD**. Esto condiciona la decisión: se acepta pagar complejidad a
cambio de que las fronteras arquitectónicas sean visibles y se noten al trabajar, incluso
cuando un proyecto de este tamaño no las necesitaría.

Dicho esto, el dominio no es un ejemplo de juguete. Tiene sustancia real:

- **Reparto de importes sin perder céntimos.** 10,00 € entre 3 no son 3,33 € × 3 = 9,99 €.
  Los céntimos sobrantes hay que asignarlos de forma determinista.
- **Invariante de agregado.** La suma de las partes de un gasto debe ser exactamente el total.
- **Simplificación de deudas.** Si A debe a B y B debe a C, la solución razonable suele ser
  que A pague directamente a C. Minimizar el número de transferencias es NP-hard en el caso
  general y requiere una heurística.
- **Aritmética monetaria.** `BigDecimal` con escala y redondeo explícitos; `double` está
  descartado de entrada.

Esa lógica es exactamente el tipo de código que se quiere poder probar y razonar sin base de
datos y sin framework por delante.

Las alternativas consideradas:

**Arquitectura por capas clásica** (`controller` → `service` → `repository`). Es lo habitual
en Spring Boot y sería más rápida de escribir. Se descarta porque el `service` acaba
dependiendo de `JpaRepository`, la entidad JPA se convierte de facto en el modelo de negocio,
y probar el cálculo de balances termina exigiendo `@SpringBootTest` con base de datos. Además,
no ejercita nada de lo que se quiere practicar.

**Vertical slices / feature folders.** Buena opción para equipos con muchas features poco
acopladas. Se descarta aquí porque el valor de este proyecto está en un núcleo de dominio
compartido (balances, deudas) que atraviesa todas las features; trocearlo por feature lo
dispersaría.

**Hexagonal con módulos Maven separados** (un módulo por capa, la regla de dependencia
impuesta por el propio build). Es la variante más estricta y la que mejor garantiza el
aislamiento. Se descarta **por ahora**: multiplica la fricción del build por una ganancia que,
con una sola persona en el proyecto, aporta poco. Queda como evolución natural si la
disciplina manual resulta insuficiente.

## Decisión

Adoptamos **arquitectura hexagonal (puertos y adaptadores)** con tres capas en un único módulo
Maven, y vocabulario de DDD para nombrar los elementos del dominio.

**1. Tres capas con la regla de dependencia apuntando hacia dentro:**

```
infrastructure  ──►  application  ──►  domain
```

`domain` no importa nada fuera de `java.*`. `application` solo conoce `domain`.
`infrastructure` conoce a las dos.

**2. El dominio es Java plano.** Sin Spring, sin JPA, sin Jackson y **sin Lombok**. Lombok se
incluye explícitamente en la prohibición: aunque desaparece al compilar, es un procesador de
anotaciones externo, y permitirlo erosiona la regla poco a poco. En `infrastructure` sí se usa.

**3. Entidades con identidad, value objects como records.** `Usuario`, `Grupo` y `Gasto` son
clases con `equals`/`hashCode` por `id` e invariantes validadas en el constructor. `Dinero`,
`DivisionGasto`, `Balance` y `PagoSugerido` son records inmutables.

**4. Puertos de entrada explícitos.** Cada caso de uso es una **interfaz** en
`application/port/in` con su implementación en `application/usecase`:

```java
// application/port/in/CrearGastoUseCase.java
public interface CrearGastoUseCase {
    GastoResult ejecutar(CrearGastoCommand command);
}

// application/usecase/CrearGastoService.java
@Service
public class CrearGastoService implements CrearGastoUseCase { ... }
```

Los controllers dependen de la interfaz, nunca de la implementación. Esto no es ceremonia
gratuita: permite mockear el caso de uso completo en un `@WebMvcTest` y hace que el conjunto
de interfaces de `port/in` sea la descripción ejecutable de qué hace la aplicación.

**5. Puertos de salida con inversión de dependencias.** Las interfaces (`GastoRepositoryPort`…)
viven en `application/port/out`; los adaptadores que las implementan, en
`infrastructure/persistence/adapter`. Los puertos hablan el lenguaje del dominio: nada de
`Page`, `Pageable` ni entidades JPA en sus firmas.

**6. Modelos separados en cada frontera:** `Request` (REST) → `Command` (application) →
modelo de dominio → entidad JPA, con mapeo explícito escrito a mano.

**7. Los domain services se instancian con `new`**, no se inyectan. `CalculadoraBalances` y
`SimplificadorDeudas` no tienen estado ni dependencias; convertirlos en beans obligaría al
dominio a conocer Spring sin ninguna ganancia.

**8. Un solo módulo Maven.** La regla de dependencia se mantiene por disciplina y revisión, no
por el build.

## Consecuencias

### Positivas

- **La lógica de negocio se prueba con JUnit puro**: sin contexto de Spring, sin Docker, sin
  base de datos. Los tests de `CalculadoraBalances` y `SimplificadorDeudas` arrancan en
  milisegundos, lo que hace viable escribir muchos casos de borde de reparto de céntimos.
- **Detector de fugas incorporado**: si un test de negocio empieza a necesitar
  `@SpringBootTest`, es porque una regla se ha escapado del dominio. La arquitectura avisa sola.
- **La infraestructura es reemplazable.** Cambiar PostgreSQL por otro almacén, o REST por
  mensajería, es escribir un adaptador nuevo sin tocar el núcleo.
- **El dominio es legible como documentación.** Las interfaces de `port/in` describen qué hace
  la aplicación sin ruido técnico.
- **Se ejercita lo que se quería practicar**: puertos, adaptadores, agregados, invariantes,
  inversión de dependencias.

### Negativas

- **Verbosidad real.** Un gasto se representa cuatro veces (`Request`, `Command`, dominio,
  entidad JPA) con mapeo manual entre ellas. Para un CRUD simple esto es puro coste; se acepta
  conscientemente.
- **Más clases por cada funcionalidad.** Añadir un caso de uso implica interfaz + implementación
  + command + result, mínimo. La curva de "líneas por feature" es notablemente más alta que en
  capas clásicas.
- **Sobredimensionado para el tamaño actual del problema.** Si el objetivo fuera entregar rápido
  y no aprender, un enfoque por capas sería la elección correcta.
- **La regla de dependencia no está forzada por herramientas.** Con un solo módulo Maven, nada
  impide técnicamente que alguien importe `infrastructure` desde `domain`. Se sostiene por
  disciplina.
- **Se pierden atajos cómodos de Spring Data.** No se pueden devolver `Page<T>` ni proyecciones
  directamente desde los puertos; hay que traducirlos.

### Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Que la regla de dependencia se erosione sin que nadie lo note | Añadir un test de **ArchUnit** que falle si `domain` importa algo fuera de `java.*` |
| Que el mapeo manual se vuelva tedioso y se empiece a saltar | Si pasa: evaluar MapStruct **solo** en `infrastructure`, nunca para cruzar hacia el dominio |
| Que la separación de módulos acabe siendo necesaria | Migrar a multi-módulo Maven en un ADR posterior; la estructura de paquetes actual ya lo permite sin reescribir |
| Que la verbosidad frene el avance del proyecto | Revisar esta decisión si llega a bloquear; el proyecto es de aprendizaje y el criterio es lo aprendido, no la velocidad |

### Nota sobre el toolchain de Java

Decisión de build relacionada, registrada aquí para no perderla: el proyecto fija
`maven.compiler.release=21`, lo que **garantiza bytecode compatible con Java 21 sin necesitar
el JDK físico de 21** — el build funciona igual si `JAVA_HOME` apunta a un JDK más nuevo (esta
máquina tiene también un JDK 23 instalado). El JDK 21 físico solo se requiere si se activa
explícitamente el perfil `jdk21-toolchain`, que está desactivado por defecto porque
`maven-toolchains-plugin` hace fallar el build cuando no encuentra un `toolchains.xml`.
