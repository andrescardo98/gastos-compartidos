package com.gastoscompartidos.domain.service;

import com.gastoscompartidos.domain.model.Balance;
import com.gastoscompartidos.domain.model.Gasto;

import java.util.List;
import java.util.UUID;

/**
 * Domain Service: calcula el saldo neto de cada miembro de un grupo.
 *
 * <p>Es un domain service (y no un metodo de Grupo o de Gasto) porque la operacion
 * cruza varios agregados: necesita el grupo Y todos sus gastos. No pertenece a ninguno
 * de los dos en exclusiva.
 *
 * <p><b>Sin estado y sin dependencias.</b> No se anota con {@code @Service} ni se
 * inyecta nada: se instancia con {@code new} desde la capa application. Asi el dominio
 * sigue sin conocer Spring y los tests son JUnit puro, sin contexto.
 *
 * <p>Formula por usuario, fijada en el ADR 0003:
 * <pre>
 *     saldo(u) = SUMA(importe de los gastos que u pago)
 *              - SUMA(parte que le corresponde a u en cada gasto)
 *              + SUMA(liquidaciones que u entrego a otros)
 *              - SUMA(liquidaciones que u recibio de otros)
 * </pre>
 *
 * <p><b>La firma de {@link #calcular} va a cambiar al implementarla.</b> Hoy solo recibe
 * miembros y gastos, asi que los dos ultimos terminos de la formula no se pueden calcular.
 * El ADR 0003 la fija en:
 * <pre>
 *     calcular(List&lt;UUID&gt; miembrosIds,
 *              List&lt;Gasto&gt; gastos,
 *              List&lt;Liquidacion&gt; liquidaciones,
 *              Currency monedaDelGrupo)
 * </pre>
 * La moneda se pasa aparte porque hace falta para devolver saldos de cero en la moneda
 * correcta a los miembros sin ninguna actividad, aunque el grupo no tenga ni un gasto del
 * que deducirla. Si algun dia {@code Grupo} gana un campo {@code moneda}, ese parametro
 * sobra: ver la condicion de revision del ADR 0003.
 *
 * <p>Reglas que el ADR 0003 fija y que hay que respetar al implementar:
 * <ul>
 *   <li>Un grupo, una sola moneda: gasto o liquidacion en otra moneda -&gt; DomainException.</li>
 *   <li>Gasto o liquidacion que involucre a alguien fuera de {@code miembrosIds} -&gt;
 *       DomainException. Ignorarlo en silencio romperia la invariante de suma cero.</li>
 * </ul>
 *
 * <p>Detalle completo en docs/decisions/0003-reglas-calculo-balances-y-reparto.md.
 */
public class CalculadoraBalances {

    /**
     * TODO: implementar.
     *
     * <p>Pasos:
     * <ol>
     *   <li>Inicializar el saldo de cada miembro a cero (importante: los miembros sin
     *       ningun gasto tambien deben aparecer, con saldo 0).</li>
     *   <li>Por cada gasto: sumar el total al pagador, restar {@code gasto.parteDe(u)}
     *       a cada participante.</li>
     *   <li>Por cada liquidacion: sumar el importe al pagador y restarselo al receptor.
     *       Entregar dinero reduce lo que debes; cobrarlo reduce lo que te deben.</li>
     *   <li>Devolver la lista de balances.</li>
     * </ol>
     *
     * <p>Asercion que debe cumplirse siempre (buen test de propiedad):
     * la suma de todos los saldos devueltos es exactamente cero. Cuidado: esa asercion
     * <b>no</b> detecta que se olviden las liquidaciones, porque cada una suma a uno lo
     * mismo que resta al otro. Hace falta un caso con liquidacion cuyo resultado esperado
     * difiera del mismo caso sin ella.
     *
     * @param miembrosIds miembros del grupo, incluidos los que no participan en ningun gasto
     * @param gastos      todos los gastos del grupo
     */
    public List<Balance> calcular(List<UUID> miembrosIds, List<Gasto> gastos) {
        throw new UnsupportedOperationException("TODO: implementar calculo de balances");
    }
}
