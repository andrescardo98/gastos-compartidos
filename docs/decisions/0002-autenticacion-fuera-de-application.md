# 0002 — Mantener la autenticación fuera de `application`

## Estado

Aceptada — 2026-08-11

## Contexto

El [ADR 0001](0001-arquitectura-hexagonal.md) fija, en su punto 4, que **cada caso de uso es
una interfaz en `application/port/in`** y que los controllers dependen siempre de esa interfaz,
nunca de una implementación. Esa regla se pensó para las operaciones del dominio: crear un
gasto, calcular balances, simplificar deudas.

La autenticación no encaja ahí, y conviene dejar por escrito por qué antes de implementar el
login — el `AuthController` ya está esbozado y su javadoc remite a esta decisión.

**El dominio de este proyecto es "repartir gastos dentro de un grupo".** Su lenguaje ubicuo son
`Gasto`, `Grupo`, `Dinero`, `Balance`, `PagoSugerido`, `DivisionGasto`. Emitir un JWT, comparar
un hash BCrypt o validar la firma de un token no forman parte de ese lenguaje: son el mecanismo
que decide *quién* puede invocar las operaciones, no una de las operaciones. Un contable que
supiera todo sobre repartir gastos no reconocería "token" como concepto de su negocio.

`Usuario` sí es una entidad del dominio, pero lo es en su papel de **miembro de un grupo y
pagador de gastos**. Que además lleve un `passwordHash` es un detalle de almacenamiento: el
dominio guarda el hash y nunca lo interpreta, no sabe con qué algoritmo se produjo ni cómo se
verifica.

Estado actual de las piezas implicadas:

| Pieza | Dónde vive | Estado |
|---|---|---|
| `AuthController` | `infrastructure/rest/controller` | esbozado, `login` con TODO |
| `JwtService` | `infrastructure/security` | esbozado, 4 métodos con TODO |
| `JwtAuthenticationFilter` | `infrastructure/security` | **pass-through**, no autentica nada todavía |
| `SecurityConfig` | `infrastructure/security` | implementado (es cableado, no negocio) |
| `UsuarioRepositoryPort` | `application/port/out` | interfaz definida; `buscarPorEmail` y `existePorEmail` anotados como "usado por el login" |

Hay además una restricción técnica dura: el login necesita `AuthenticationManager` y
`PasswordEncoder`, que son **tipos de Spring Security**. Un caso de uso en `application` que los
recibiera arrastraría el framework de seguridad hacia el interior del hexágono, que es
exactamente lo que el ADR 0001 pretende evitar.

## Decisión

**`AuthController` habla directamente con `JwtService`, `AuthenticationManager`,
`PasswordEncoder` y `UsuarioRepositoryPort`, sin pasar por ningún caso de uso de
`application`.** La autenticación se resuelve entera dentro de `infrastructure`.

```
AuthController ──► AuthenticationManager  (Spring Security)
      │        ──► PasswordEncoder        (Spring Security)
      │        ──► JwtService             (infrastructure/security)
      └──────────► UsuarioRepositoryPort  (application/port/out)
```

Cuatro precisiones que delimitan el alcance:

**1. Esto no rompe la regla de dependencia.** `infrastructure` conoce `application` y `domain`;
usar `UsuarioRepositoryPort` desde un controller es una dependencia hacia dentro, perfectamente
legal. Lo que se hace aquí es **una excepción a una convención** (todo controller pasa por un
`port/in`), no una excepción a la arquitectura. La flecha sigue apuntando hacia dentro.

**2. `port/in` sigue siendo la descripción de qué hace la aplicación.** El ADR 0001 justifica los
puertos de entrada diciendo que el conjunto de interfaces de `port/in` es "la descripción
ejecutable de qué hace la aplicación". Precisamente por eso no se añade `LoginUseCase`: metería
en esa lista una capacidad que no es del negocio y emborronaría el catálogo.

**3. El dominio sigue sin saber que existen los tokens.** Se mantiene lo ya anotado en el javadoc
de `JwtService`: cuando un caso de uso necesite saber quién actúa, **recibe el `UUID` del usuario
como parámetro** de su command o query. Ningún elemento de `application` o `domain` lee el
`SecurityContext` ni acepta un token como argumento. El controller extrae la identidad y la pasa
como dato plano.

**4. Condición explícita de revisión.** Esta decisión vale mientras autenticar y registrar sean
puramente técnicos. **En cuanto el registro adquiera reglas de negocio** — invitaciones a un
grupo, cuotas de usuarios, verificación de email, políticas de expulsión y readmisión — esa parte
deja de ser transversal y hay que promoverla a un puerto de entrada propio
(p. ej. `RegistrarUsuarioUseCase`), dejando en `infrastructure` solo la emisión del token.
La unicidad de email **no** cuenta como regla de negocio a estos efectos: es una restricción de
integridad, y por eso `existePorEmail` vive en el puerto de salida.

## Consecuencias

### Positivas

- **No se inventan abstracciones de un solo uso.** No hace falta un `TokenGeneratorPort` en
  `port/out` cuya única implementación sería `JwtService` y cuyo único cliente sería el login.
- **Spring Security no cruza hacia dentro.** `AuthenticationManager` y `PasswordEncoder` se
  quedan donde ya vive el resto del cableado de seguridad.
- **Menos código sin sustancia.** Un `LoginUseCase` completo son cuatro clases (interfaz,
  implementación, command, result) sin una sola regla de dominio que probar.
- **El catálogo de `port/in` se mantiene honesto**: contiene solo capacidades de "gastos
  compartidos", que es lo que lo hace útil como documentación.
- **La seguridad queda concentrada en un sitio.** Todo lo relativo a credenciales y tokens está
  en `infrastructure/security` más `AuthController`, que es donde alguien lo buscaría.

### Negativas

- **Es una asimetría en el código.** Todos los controllers dependen de un `port/in` menos uno.
  Quien lea `AuthController` sin este ADR pensará que es un descuido — de ahí el javadoc que
  apunta aquí.
- **El test del controller es más pesado.** Un `@WebMvcTest` de `AuthController` no puede mockear
  un único caso de uso: hay que mockear `AuthenticationManager`, `JwtService` y
  `UsuarioRepositoryPort` por separado.
- **Riesgo de que se acumule lógica en el controller.** Sin un caso de uso que sirva de destino
  natural, la tentación de ir añadiendo pasos al método `login` es real.
- **La frontera es de criterio, no de compilador.** Nada impide técnicamente que mañana entre
  una regla de negocio en `AuthController`; solo lo evita la revisión.

### Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Que el registro acumule reglas de negocio dentro del controller sin que nadie lo note | Regla de revisión: si `login` o `registro` necesitan consultar algo distinto de `Usuario`, es señal de promover a `port/in` |
| Que alguien replique el atajo en otros controllers ("aquí tampoco hace falta caso de uso") | Este ADR delimita la excepción a autenticación; cualquier otra requiere ADR propio |
| Que un caso de uso empiece a leer el `SecurityContext` para saber quién actúa | Los commands y queries llevan el `UUID` del actor como campo explícito; se revisa en cada caso de uso nuevo |
| Que `JwtAuthenticationFilter` se quede como pass-through y dé falsa sensación de seguridad | Está anotado en `NOTES.md` como pendiente; hoy los endpoints responden 401 solo porque el contexto queda vacío |

## Alternativas consideradas

**Crear un `LoginUseCase` en `application/port/in`** (con `LoginService` en `application/usecase`,
más `LoginCommand` y `TokenResult`). Es la opción coherente con el patrón del resto del proyecto,
y por eso se evaluó primero. Se descarta por cuatro motivos que se acumulan:

1. **El caso de uso sería un pasamanos vacío.** Su cuerpo sería "verifica credenciales, emite
   token": dos pasos cuya sustancia entera es técnica. No habría ninguna invariante de dominio
   que proteger, que es la razón por la que existen los casos de uso.
2. **Arrastraría el framework hacia dentro.** `AuthenticationManager` y `PasswordEncoder` son
   tipos de Spring Security; `application` pasaría a depender de él. Evitarlo obligaría a
   inventar `TokenGeneratorPort` y `VerificadorDeCredencialesPort` en `port/out`, dos
   abstracciones con una sola implementación y un solo cliente, creadas únicamente para
   satisfacer la convención.
3. **Contaminaría el catálogo de `port/in`.** Ese catálogo se justifica en el ADR 0001 como la
   lista legible de lo que hace la aplicación. `LoginUseCase` sugeriría que autenticarse es una
   capacidad del negocio "gastos compartidos", cuando es la puerta de entrada a él.
4. **Coste sin retorno medible.** Cuatro clases más y un mapeo más, a cambio de simetría visual.

Queda como la alternativa a retomar si se cumple la condición de revisión del punto 4 de la
Decisión.

**Un domain service `ServicioDeAutenticacion` en `domain`.** Descartada de inmediato: el dominio
es Java plano y no puede conocer BCrypt, jjwt ni Spring. Tendría que recibir todo por
inyección de interfaces propias, reproduciendo el problema anterior una capa más adentro y
rompiendo además la prohibición de que `domain` importe algo fuera de `java.*`.

**Meter la verificación de contraseña dentro de la entidad `Usuario`**
(`usuario.verificarPassword(String)`). Atractiva porque parece encapsulación de libro, pero
obligaría al dominio a conocer el algoritmo de hash — hoy BCrypt, mañana Argon2 — y convertiría
un detalle de infraestructura en una regla del agregado. El dominio guarda el hash como opaco y
así se queda.

**Delegar todo en la configuración por defecto de Spring Security, sin `AuthController`**
(formulario de login o Basic). Descartada porque la API es stateless y el cliente necesita un
JWT en el cuerpo de la respuesta, con su tiempo de expiración; eso requiere un endpoint propio.
`httpBasic` se mantiene activo en `SecurityConfig` solo como comodidad de desarrollo.

**Un módulo o paquete `auth` hermano del hexágono**, fuera de `domain`/`application`/
`infrastructure`. Es defendible en sistemas donde la identidad es un subdominio con entidad
propia. Se descarta por tamaño: aquí la autenticación son dos endpoints y un filtro, y añadir
una cuarta raíz de paquetes costaría más claridad de la que daría.
