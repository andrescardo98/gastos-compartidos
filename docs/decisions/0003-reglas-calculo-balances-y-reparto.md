# 0003 — Reglas de cálculo de balances y reparto de importes

## Estado

Aceptada — 2026-08-19

## Contexto

El esqueleto del dominio está completo, pero las tres piezas que de verdad hacen el trabajo
—repartir un importe, calcular quién debe cuánto y decidir quién le paga a quién— siguen siendo
`TODO`. Antes de escribir el primer test hace falta fijar por escrito **qué se espera de ellas**,
porque un test que no parte de una regla acordada no verifica nada: consagra la primera
implementación que se nos haya ocurrido.

Las tres piezas son:

| Pieza | Dónde vive | Qué resuelve |
|---|---|---|
| `Dinero.repartirEn(int)` | `domain/model/Dinero.java` | dividir un importe entre N personas sin perder céntimos |
| `CalculadoraBalances` | `domain/service/CalculadoraBalances.java` | cuánto debe o le deben a cada miembro del grupo |
| `SimplificadorDeudas` | `domain/service/SimplificadorDeudas.java` | qué transferencias saldan todas las deudas |

Están encadenadas: la tercera consume la salida de la segunda, y la segunda depende de que la
primera no pierda dinero por el camino. Un error en `repartirEn` se propaga hasta la lista final
de pagos, así que conviene acordar las tres a la vez.

El orden de implementación previsto en `NOTES.md` (puntos 1, 3 y 4 de *Pendiente: el trabajo
real*) es exactamente ese. Este ADR cubre además la deuda ya anotada en la sección 7 de
[`docs/architecture.md`](../architecture.md): *"Heurística greedy en `SimplificadorDeudas` (no
garantiza el mínimo absoluto de transferencias)"*.

### Glosario mínimo

Este documento debería poder leerlo alguien que se incorpore al equipo hoy, o alguien de negocio
sin fondo técnico. Cuatro palabras que van a aparecer, explicadas una sola vez:

- **Invariante**: una regla que siempre se tiene que cumplir, pase lo que pase. Sirve como
  detector de errores: si deja de cumplirse, hay un fallo en alguna parte, aunque todavía no
  sepamos dónde.
- **Determinista**: que da siempre exactamente el mismo resultado con los mismos datos de
  entrada, por muchas veces que se repita. Lo contrario sería que el resultado cambiase de una
  ejecución a otra.
- **Céntimo** (o *unidad mínima*): la porción más pequeña de dinero que la moneda admite. En
  euros y dólares son dos decimales; en yenes, ninguno. Aquí se usa "céntimo" como término
  genérico para "la unidad más pequeña que la moneda permite representar".
- **Escala**: cuántos decimales tiene una moneda. Es lo que devuelve
  `Currency.getDefaultFractionDigits()` en Java.

## Decisión

Se fijan las tres reglas siguientes. Todas viven en `domain/`, sin Spring y sin dependencias
externas, y todas son deterministas: los tests pueden comparar contra un resultado exacto y
esperado, sin tolerancias ni "aproximadamente".

---

### 1. `Dinero.repartirEn(n)` — dividir un importe entre N personas

**La idea, en una frase:** cuando divides una cuenta entre varias personas casi nunca sale un
número redondo de céntimos, así que alguien tiene que quedarse con el céntimo que sobra, y hay
que decidir de antemano quién, para que no dependa del azar.

**La regla:**

1. Se calcula la parte de cada persona **redondeando siempre hacia abajo** al céntimo.
2. Los céntimos que sobran se reparten **de uno en uno**, empezando por la primera persona de la
   lista y siguiendo el orden en que se entregó esa lista.

Dicho de otro modo: nunca se reparte más de un céntimo extra por persona, y quienes lo reciben
son siempre los primeros de la lista.

**Ejemplo:** $100.00 repartidos entre 3 personas — Ana, Luis y Marta, en ese orden.

| Persona | Posición | Parte base (hacia abajo) | Céntimo extra | Paga |
|---|---|---|---|---|
| Ana | 1.ª | $33.33 | +$0.01 | **$33.34** |
| Luis | 2.ª | $33.33 | — | **$33.33** |
| Marta | 3.ª | $33.33 | — | **$33.33** |
| | | | **Total** | **$100.00** |

Al dividir 100.00 entre 3 salen 33.33 para cada uno y queda 1 céntimo sin asignar. Ese céntimo va
a Ana por ser la primera de la lista.

**La garantía (invariante):** la suma de las partes es **exactamente** el importe original. Nunca
sobra ni falta un céntimo, para ningún importe y para ningún número de personas.

**Cómo se implementa, en pseudocódigo:**

```
total_en_centimos = importe convertido a la unidad mínima de la moneda   (entero)
base  = total_en_centimos / n     (división entera, descarta el resto)
resto = total_en_centimos % n     (los céntimos que sobran; siempre 0 <= resto < n)

para i de 0 a n-1:
    parte[i] = base + (1 si i < resto, si no 0)
```

Se trabaja con enteros de céntimos precisamente para que la suma cuadre por construcción y no por
suerte de redondeo: `base * n + resto == total_en_centimos` es aritmética exacta.

**Precisiones que delimitan el alcance:**

- **El emparejamiento posición ↔ persona es responsabilidad de quien llama.** `repartirEn`
  devuelve una lista de importes; no sabe quién es Ana. Es `Gasto.crearEquitativo` quien recorre
  `participantesIds` y la lista devuelta a la vez, en el mismo orden. Ese orden es el que el
  usuario envió en la petición, y hay que preservarlo sin reordenar por el camino.
- **Con `n <= 0` se lanza `DomainException`** (subtipo `DivisionInvalidaException`). Repartir
  entre cero personas no significa nada; no se devuelve una lista vacía en silencio.
- **Se admite importe cero** (produce N partes de cero) y **se rechaza importe negativo**: un
  gasto negativo no existe. Los saldos sí pueden ser negativos, pero los saldos no se reparten.
- **La moneda se conserva**: las N partes salen con la misma moneda que el importe original.
- **Redondeo hacia abajo, no al más cercano.** Con importes no negativos, "hacia abajo" (`FLOOR`)
  y "truncar" (`DOWN`) coinciden. Se documenta como `FLOOR` para que la intención quede explícita
  si algún día entrase un importe negativo por otra vía.
- **Trampa a verificar antes de fiarse:** la escala la decide `Currency`, no nosotros. Java
  asigna 2 decimales al peso colombiano (`COP`), aunque en la práctica en Colombia no se usen
  céntimos. Antes de dar por buena la escala de cualquier moneda, comprobarla en un test — no
  asumirla.

---

### 2. `CalculadoraBalances` — cuánto debe o le deben a cada persona

**La idea, en una frase:** al final, cada persona del grupo tiene un número que dice si le deben
dinero (positivo) o si debe dinero (negativo); ese número sale de sumar lo que puso, restar lo
que le tocaba poner, y ajustar con los pagos que ya se hayan hecho para saldar cuentas.

**La fórmula, con cada término explicado:**

```
balance(persona) =   lo que pagó en gastos
                   − lo que le tocaba pagar de su parte
                   + lo que entregó a otros para saldar deudas
                   − lo que otros le entregaron a ella para saldar una deuda
```

| Término | Qué significa | De dónde sale |
|---|---|---|
| lo que pagó en gastos | el importe **total** de cada gasto que esa persona adelantó | `Gasto.getPagadorId()` + `getImporteTotal()` |
| lo que le tocaba pagar | su parte en cada gasto en el que participa, haya pagado o no | `Gasto.parteDe(usuarioId)` |
| lo que entregó para saldar | pagos ya hechos por ella a otro miembro | `Liquidacion` donde es `pagadorId` |
| lo que le entregaron | pagos ya recibidos de otro miembro | `Liquidacion` donde es `receptorId` |

Los dos primeros términos son el gasto compartido; los dos últimos, las cuentas ya saldadas. Una
liquidación **suma** a quien paga porque entregar dinero mejora tu posición: reduce lo que debes.
Y **resta** a quien lo recibe porque cobrar reduce lo que te deben.

**Ejemplo paso a paso: un viaje entre Ana, Luis y Marta.**

Ana paga el hotel: **$90.000**, repartido en partes iguales entre los tres → **$30.000** cada uno.

Paso 1 — solo el gasto:

| Persona | Pagó | Le tocaba | Balance |
|---|---|---|---|
| Ana | $90.000 | $30.000 | **+$60.000** (le deben) |
| Luis | $0 | $30.000 | **−$30.000** (debe) |
| Marta | $0 | $30.000 | **−$30.000** (debe) |
| | | **Suma** | **$0** |

Ana adelantó $90.000 pero solo le correspondían $30.000, así que el grupo le debe $60.000. Luis y
Marta no pusieron nada y les tocaban $30.000 a cada uno.

Paso 2 — Luis le paga a Ana **$30.000** como liquidación:

| Persona | Balance anterior | Ajuste por la liquidación | Balance final |
|---|---|---|---|
| Ana | +$60.000 | −$30.000 (le pagaron) | **+$30.000** |
| Luis | −$30.000 | +$30.000 (pagó) | **$0** (en paz) |
| Marta | −$30.000 | sin cambios | **−$30.000** |
| | | **Suma** | **$0** |

Luis queda a cero: ya saldó lo suyo. Ana pasa de que le deban $60.000 a que le deban $30.000, que
es justo lo que le falta cobrarle a Marta.

**La garantía (invariante): la suma de los balances de todo el grupo es siempre exactamente
cero.** El dinero no se crea ni se destruye, solo cambia de manos: si alguien está en positivo,
hay alguien en negativo por la misma cantidad. Se cumple por construcción, porque cada gasto suma
su total al pagador y resta ese mismo total repartido entre los participantes —y ahí es donde
importa la garantía de `repartirEn`: si el reparto perdiera un céntimo, la suma dejaría de dar
cero—, y cada liquidación suma a uno exactamente lo que resta al otro.

Es el mejor test que tenemos: si un día la suma no da cero, hay un fallo, aunque el resultado
"parezca" razonable.

**Regla adicional: un grupo, una sola moneda.** Todos los gastos y todas las liquidaciones de un
mismo grupo tienen que estar en la misma moneda. Si alguien intenta mezclar pesos colombianos con
dólares en el mismo grupo, el sistema lo **rechaza** con `DomainException`. El motivo es que sin
una tasa de cambio la operación no significa nada: sumar 100 dólares y 100 pesos no da 200 de
nada. Y una tasa de cambio no es un dato del dominio "repartir gastos": cambia cada día, habría
que decidir la fecha de conversión y de dónde se obtiene. Convertir monedas es otro problema, y
si algún día se aborda será con su propio ADR.

**Precisiones que delimitan el alcance:**

- **Los miembros sin actividad aparecen con saldo cero.** La lista de balances incluye a todos
  los miembros del grupo, no solo a los que participaron en algún gasto. Un balance ausente y un
  balance de cero se leen igual en pantalla, pero solo el segundo demuestra que se ha tenido en
  cuenta a esa persona.
- **Se rechaza a los desconocidos.** Si un gasto o una liquidación involucra a alguien que no
  está en la lista de miembros del grupo, se lanza `DomainException`. Ignorarlo en silencio
  rompería la invariante de suma cero, y el error aparecería mucho más tarde y muy lejos de su
  causa.
- **Grupo sin gastos ni liquidaciones**: todos a cero. La suma sigue dando cero, trivialmente.
- **La moneda del grupo se pasa como parámetro explícito.** Hace falta para poder devolver saldos
  de cero *en la moneda correcta* a los miembros sin actividad, incluso cuando no hay ni un solo
  gasto del que deducirla. *Condición de revisión:* si en el futuro `Grupo` gana un campo
  `moneda` —lo natural cuando exista el endpoint de creación de grupo—, ese parámetro sobra y hay
  que quitarlo; la validación de moneda única pasaría entonces a ser una invariante del agregado
  `Grupo`, comprobada al añadir cada gasto.

**Cambio de firma que este ADR autoriza.** Hoy el método es:

```java
List<Balance> calcular(List<UUID> miembrosIds, List<Gasto> gastos)
```

Incorporar las liquidaciones y la moneda obliga a:

```java
List<Balance> calcular(List<UUID> miembrosIds,
                       List<Gasto> gastos,
                       List<Liquidacion> liquidaciones,
                       Currency monedaDelGrupo)
```

Es exactamente el cambio que `NOTES.md` y el javadoc de `Liquidacion` dejaron anotado como
pendiente ("`CalculadoraBalances` sigue recorriendo solo los gastos"). Al hacerlo hay que
actualizar también esas dos notas y el javadoc de la propia clase, cuya fórmula actual solo
menciona los gastos.

---

### 3. `SimplificadorDeudas` — quién le paga a quién, con los menos pagos posibles

**La idea, en una frase:** una vez sabemos cuánto debe o le deben a cada uno, en vez de que todos
se paguen entre todos, buscamos la forma más simple de dejar las cuentas a cero: el menor número
razonable de transferencias.

**El método:** se empareja repetidamente a **quien más debe** con **quien más le deben**, se
transfiere entre ellos la mayor cantidad que tenga sentido —la menor de las dos cifras— y se
repite con lo que quede, hasta que nadie deba nada.

En pseudocódigo:

```
deudores   = personas con saldo negativo, ordenadas por deuda de mayor a menor
acreedores = personas con saldo positivo, ordenadas por crédito de mayor a menor
(los saldos de cero se descartan: no participan)

mientras queden deudores y acreedores:
    d = el que más debe
    a = al que más le deben
    importe = el menor de (deuda de d, crédito de a)
    emitir el pago: d transfiere `importe` a a
    restar `importe` a ambos
    quien quede en cero sale; quien no, vuelve a la cola
```

**Ejemplo paso a paso: viaje a Río de Janeiro entre Andrés, Camilo y Sofía.**

Los gastos del viaje fueron:

| Gasto | Lo pagó | Importe | Repartido entre | Parte de cada uno |
|---|---|---|---|---|
| Hotel | Andrés | $900.000 | los 3 | $300.000 |
| Cena de despedida | Sofía | $150.000 | los 3 | $50.000 |

Balances según la regla 2 (a cada uno le tocaban $300.000 + $50.000 = $350.000):

| Persona | Pagó | Le tocaba | Balance |
|---|---|---|---|
| Andrés | $900.000 | $350.000 | **+$550.000** |
| Camilo | $0 | $350.000 | **−$350.000** |
| Sofía | $150.000 | $350.000 | **−$200.000** |
| | | **Suma** | **$0** |

Ahora el simplificador. Deudores: Camilo ($350.000) y Sofía ($200.000). Acreedores: Andrés
($550.000).

| Paso | Quien más debe | A quien más le deben | Se transfiere | Cómo quedan |
|---|---|---|---|---|
| 1 | Camilo, −$350.000 | Andrés, +$550.000 | $350.000 (el menor de los dos) | Camilo a **0**, sale. Andrés baja a +$200.000 |
| 2 | Sofía, −$200.000 | Andrés, +$200.000 | $200.000 | Ambos a **0**. Fin |

Resultado: **2 pagos**.

```
Camilo  ──$350.000──►  Andrés
Sofía   ──$200.000──►  Andrés
```

Con 3 personas, 2 pagos es el mínimo posible siempre que nadie esté ya a cero: hacen falta al
menos tantas transferencias como personas menos una. Nadie paga por partida doble, y Sofía no
tiene que pagarle a Camilo ni al revés aunque los dos hayan puesto dinero.

**Sobre el método: es "greedy" y eso tiene un límite conocido.** *Greedy* (voraz) significa que en
cada paso se toma la mejor decisión posible **en ese momento**, sin planificar los pasos futuros.
Aquí eso es: saldar de golpe la deuda más grande que se pueda.

Encontrar el número **mínimo absoluto** de transferencias es un problema NP-duro (se reduce a
partición de conjuntos): no hay algoritmo eficiente conocido que lo resuelva siempre. El método
voraz **no garantiza matemáticamente ese mínimo absoluto** —cuando existen subgrupos de personas
cuyos saldos se cancelan exactamente entre sí, puede romperlos y generar algún pago de más—,
pero:

- deja **como mucho n−1 pagos** para n personas con saldo distinto de cero, que es lo que la
  gente espera y considera razonable;
- es rápido y fácil de explicar a un usuario ("el que más debe le paga al que más le deben");
- en grupos reales, de 3 a 10 personas, la diferencia con el óptimo es nula o de un pago.

**Se acepta explícitamente ese trade-off.** Este proyecto es una práctica de arquitectura, no un
optimizador combinatorio, y el coste de la alternativa exacta no se justifica. Si algún día
importa, es un cambio contenido: el domain service tiene un solo método y ninguna dependencia.

**Sobre el determinismo y por qué importa para los tests.** *Determinista* quiere decir que con
los mismos datos de entrada sale siempre exactamente el mismo resultado, se ejecute las veces que
se ejecute. Sin eso no se pueden escribir tests que comparen contra una lista de pagos concreta:
el test pasaría unas veces y fallaría otras, que es la peor clase de test.

El problema aparece con los empates: si Camilo y Sofía debieran **exactamente lo mismo**, ¿a cuál
de los dos se empareja primero? Sin una regla, dependería del orden en que llegaran los datos.

**La regla de desempate: a igualdad de importe, va primero el identificador único (`UUID`)
menor**, comparado con `UUID.compareTo`. Es un criterio arbitrario a propósito —no significa nada
de negocio—, pero es estable: el mismo grupo con los mismos gastos produce siempre la misma lista
de pagos, en el mismo orden. Se aplica igual a la cola de deudores y a la de acreedores.

**Precondición y postcondiciones:**

- **Precondición**: la suma de los saldos de entrada debe ser cero. Si no lo es, se lanza
  `DomainException` sin intentar simplificar nada. No es un error del usuario: significa que
  `CalculadoraBalances` tiene un fallo, y taparlo aquí solo lo haría más difícil de encontrar.
- **Postcondición 1**: aplicar todos los pagos devueltos a los balances de entrada deja a todo el
  mundo a cero.
- **Postcondición 2**: se emiten como mucho n−1 pagos, siendo n el número de personas con saldo
  distinto de cero.
- **Postcondición 3**: ningún pago tiene importe cero, y nadie se paga a sí mismo. Ambas cosas ya
  las impide el constructor de `PagoSugerido`; el simplificador no debe llegar a intentarlo.

## Consecuencias

### Positivas

- **Los tests se pueden escribir contra valores exactos.** Al ser todo determinista, un test dice
  "el resultado es esta lista concreta", no "el resultado se le parece". Es la diferencia entre
  un test que detecta regresiones y uno decorativo.
- **Hay dos invariantes que sirven de red de seguridad**: la suma de las partes de un reparto es
  el total, y la suma de los balances de un grupo es cero. Las dos son baratas de comprobar y
  cazan una familia entera de errores de redondeo sin tener que anticiparlos uno a uno.
- **Las reglas están acordadas antes de existir el código**, así que el test no puede limitarse a
  copiar lo que la implementación hace. Es el orden que da valor a las pruebas.
- **El dominio sigue siendo Java plano.** Nada de lo decidido aquí necesita Spring, JPA ni
  librería externa: aritmética de `BigDecimal`, listas y `UUID`.
- **La decisión pendiente sobre el greedy queda cerrada**, con su justificación y su límite
  escritos, en vez de vivir como comentario suelto en un javadoc.

### Negativas

- **El reparto de céntimos es "injusto" de forma sistemática.** Quien va primero en la lista paga
  siempre el céntimo de más. En un grupo con muchos gastos y siempre el mismo orden, esa persona
  acumula unos céntimos de diferencia. Se acepta: la alternativa (rotar quién carga el resto)
  exige guardar estado entre gastos y no compensa por un importe que ronda el céntimo.
- **Cambia la firma de `CalculadoraBalances.calcular`**, y con ella el javadoc de la clase, la
  nota de `Liquidacion` y dos apartados de `NOTES.md`. Es trabajo de sincronización que hay que
  hacer completo o quedará documentación mintiendo.
- **La restricción de moneda única bloquea un caso de uso real**: un viaje al extranjero con
  gastos en dos monedas no se puede registrar en un solo grupo. El apaño es crear un grupo por
  moneda, que es peor experiencia pero al menos es correcto.
- **El resultado del simplificador puede no ser el óptimo absoluto.** Un usuario con papel y lápiz
  podría, en casos concretos, encontrar una transferencia menos.
- **La regla de desempate por `UUID` no se le puede explicar al usuario.** Si dos personas deben
  lo mismo, el orden de los pagos parecerá arbitrario, porque lo es.

### Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Que el reparto pierda o invente céntimos en algún caso raro (importes grandes, muchas personas, moneda sin decimales) | Test de propiedad sobre muchos importes y valores de N: la suma de partes es siempre el total. No basta con probar 100/3 |
| Que alguien "arregle" un desajuste de céntimos redondeando al más cercano (`HALF_UP`) en el reparto | La regla es hacia abajo + reparto del resto; `HALF_UP` solo se usa en `Dinero.por(...)` para porcentajes. Está escrito aquí y debe estarlo en el javadoc |
| Que se olvide sumar las liquidaciones al implementar los balances y el resultado parezca correcto | El test debe incluir un caso con liquidación cuyo resultado esperado sea distinto del caso sin ella. La invariante de suma cero **no** detecta este olvido: se cumple igual |
| Que la mezcla de monedas se cuele por un camino no validado (una liquidación en otra moneda) | Validar la moneda tanto en gastos como en liquidaciones, con un test por cada puerta de entrada |
| Que el simplificador entre en bucle infinito si un saldo no llega nunca a cero por un error de redondeo | Trabajar en unidades enteras de céntimo y afirmar en cada iteración que la suma pendiente decrece estrictamente |
| Que la lista de pagos cambie de orden entre ejecuciones y haga los tests intermitentes | Desempate por `UUID` documentado aquí, más un test que ejecute el mismo caso con la lista de entrada barajada y espere idéntica salida |
| Que la excepción por suma distinta de cero se convierta en un 4xx y culpe al usuario de un bug nuestro | Es una `DomainException` interna: no debe mapearse a 422 en `GlobalExceptionHandler` como si fuese entrada inválida |

## Alternativas consideradas

**Repartir el céntimo sobrante al azar, o al último de la lista.** Al azar queda descartado de
inmediato: rompe el determinismo y con él la posibilidad de testear. Al último es tan válido como
al primero —igual de arbitrario y con la misma garantía de suma— y se descarta solo por
convención: "los primeros de la lista" es más fácil de explicar y de comprobar mentalmente al leer
un test. Era una alternativa perfectamente defendible.

**Rotar quién carga con el céntimo sobrante entre gastos**, para que la injusticia se compense a
lo largo del viaje. Es más justo de verdad, y en un sistema con dinero real sería lo correcto. Se
descarta porque exige que `Dinero.repartirEn` deje de ser una función pura —necesitaría saber qué
pasó en los gastos anteriores— y eso arrastra estado a un value object que hoy es inmutable y
trivial de probar. El importe en juego es de céntimos.

**Guardar los importes como enteros de céntimos en todo el dominio**, en vez de `BigDecimal`.
Elimina de raíz los problemas de escala y de redondeo, y es lo que hacen muchos sistemas de pagos.
Se descarta porque `Dinero` ya está definido sobre `BigDecimal` en el ADR 0001 y en el esquema de
persistencia, y porque `BigDecimal` con escala explícita da las mismas garantías mientras la
escala se controle. La conversión a céntimos se hace **dentro** de `repartirEn`, que es donde
importa.

**Permitir varias monedas en un grupo, convirtiendo con una tasa de cambio.** Es lo que pediría un
usuario que viaja. Se descarta por alcance: obliga a decidir de dónde sale la tasa, con qué fecha
se congela cada gasto, qué pasa cuando la tasa cambia después de registrarlo y en qué moneda se
expresa el balance. Es un subdominio entero, con su propio puerto de salida hacia un proveedor
externo. *Condición de reversión:* si el multi-moneda entra en el alcance, esta regla se sustituye
y hace falta un ADR nuevo que fije la política de conversión; no basta con relajar la validación.

**Algoritmo exacto de mínimo número de transferencias** (búsqueda de subconjuntos que suman cero,
con programación dinámica sobre máscaras de bits). Da el óptimo garantizado y para grupos de menos
de unas 15 personas es perfectamente viable en tiempo. Se descarta porque el beneficio —ahorrar
ocasionalmente un pago— no compensa la complejidad de un algoritmo que nadie va a poder revisar de
un vistazo, en un proyecto cuyo objetivo declarado es practicar arquitectura hexagonal, no
optimización combinatoria. Queda anotado como la alternativa a retomar si el número de pagos llega
a ser una queja real.

**No simplificar: que cada deudor pague a cada acreedor su parte proporcional.** Es trivial de
implementar y no necesita heurística. Se descarta porque genera hasta deudores × acreedores pagos,
con importes de céntimos, que es justo la experiencia que la aplicación existe para evitar.

**Desempatar por nombre de usuario en vez de por `UUID`.** Produciría un orden legible para una
persona ("primero Andrés, luego Camilo"). Se descarta porque el dominio trabaja con
identificadores, no con nombres: `Balance` solo tiene `usuarioId`, y traer el nombre hasta aquí
obligaría al simplificador a conocer `Usuario` sin necesitarlo para nada más. Además el nombre es
editable, así que el orden podría cambiar sin que cambien los datos del cálculo.

## Casos que debemos probar

Guion para cuando escribamos los tests. Todavía sin código: solo la situación y el resultado
esperado.

### `Dinero.repartirEn(n)`

| Situación | Qué debe pasar |
|---|---|
| $100.00 entre 3 (Ana, Luis, Marta) | $33.34, $33.33, $33.33 — y la suma da exactamente $100.00 |
| $90.00 entre 3 | $30.00 a cada uno, sin resto que repartir |
| $100.00 entre 1 | una sola parte de $100.00 |
| $0.01 entre 3 | $0.01, $0.00, $0.00 — el primero se lleva el único céntimo, los demás reciben cero |
| $0.00 entre 4 | cuatro partes de $0.00 |
| $10.00 entre 7 | seis partes con céntimo extra y una sin él; la suma sigue siendo $10.00 |
| Un importe cualquiera entre un N cualquiera (test de propiedad, muchos valores) | la suma de las partes es siempre el importe original |
| Se llama dos veces con los mismos datos | resultado idéntico, elemento a elemento |
| n = 0 | `DivisionInvalidaException` |
| n negativo | `DivisionInvalidaException` |
| Importe negativo | `DomainException` |
| Moneda sin decimales (p. ej. JPY): 100 entre 3 | 34, 33, 33 — la unidad mínima es 1, no 0.01 |
| Moneda del resultado | todas las partes conservan la moneda del importe original |

### `CalculadoraBalances`

| Situación | Qué debe pasar |
|---|---|
| Grupo de 3, un gasto de $90.000 pagado por Ana y repartido entre los tres | Ana +$60.000, Luis −$30.000, Marta −$30.000 |
| El caso anterior, más una liquidación de $30.000 de Luis a Ana | Ana +$30.000, Luis $0, Marta −$30.000 |
| Grupo sin ningún gasto ni liquidación | todos los miembros aparecen con saldo $0 |
| Un miembro que no participa en ningún gasto | aparece en la lista, con saldo $0 (no se omite) |
| Alguien paga un gasto en el que no participa | recibe el total como crédito y no se le resta ninguna parte |
| Alguien participa en un gasto pero no lo paga | solo se le resta su parte |
| Un gasto con reparto no exacto ($100.00 entre 3) | los saldos siguen sumando exactamente cero, sin céntimo perdido |
| Varios gastos y varias liquidaciones mezclados | los saldos suman cero (test de propiedad, con datos generados) |
| Una liquidación que salda por completo una deuda | el deudor queda exactamente a $0 |
| Una liquidación mayor que la deuda | el saldo del pagador se vuelve positivo; se permite, no es un error |
| Un gasto en una moneda distinta a la del grupo | `DomainException` |
| Una liquidación en una moneda distinta a la del grupo | `DomainException` |
| Un gasto cuyo pagador no está en la lista de miembros | `DomainException` |
| Una liquidación que involucra a alguien ajeno al grupo | `DomainException` |
| Se llama dos veces con los mismos datos | resultado idéntico |

### `SimplificadorDeudas`

| Situación | Qué debe pasar |
|---|---|
| Río de Janeiro: Andrés +$550.000, Camilo −$350.000, Sofía −$200.000 | 2 pagos: Camilo→Andrés $350.000 y Sofía→Andrés $200.000 |
| Todos los saldos a cero | lista vacía de pagos |
| Un solo deudor y un solo acreedor por el mismo importe | un único pago por ese importe |
| Un deudor y dos acreedores | dos pagos, y el deudor queda a cero |
| Alguien con saldo cero mezclado entre deudores y acreedores | no aparece en ningún pago |
| Dos deudores con exactamente la misma deuda | se empareja primero el de `UUID` menor; resultado siempre igual |
| Dos acreedores con exactamente el mismo crédito | mismo criterio de desempate |
| La misma entrada, con la lista de balances barajada | exactamente la misma lista de pagos, en el mismo orden |
| Cualquier entrada válida (test de propiedad) | aplicar todos los pagos deja todos los saldos a cero |
| Cualquier entrada válida (test de propiedad) | se emiten como mucho n−1 pagos, con n = personas con saldo distinto de cero |
| Cualquier entrada válida | ningún pago con importe cero, ninguno con deudor igual a acreedor |
| Los saldos de entrada no suman cero | `DomainException` (es un bug de `CalculadoraBalances`, no entrada del usuario) |
| Saldos con céntimos que no casan (p. ej. +$33.34, −$33.33, −$0.01) | se salda igual, sin pagos de importe cero |
