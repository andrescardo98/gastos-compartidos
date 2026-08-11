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
 * <p>Formula por usuario:
 * <pre>
 *     saldo(u) = SUMA(importe de los gastos que u pago)
 *              - SUMA(parte que le corresponde a u en cada gasto)
 * </pre>
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
     *   <li>Devolver la lista de balances.</li>
     * </ol>
     *
     * <p>Asercion que debe cumplirse siempre (buen test de propiedad):
     * la suma de todos los saldos devueltos es exactamente cero.
     *
     * @param miembrosIds miembros del grupo, incluidos los que no participan en ningun gasto
     * @param gastos      todos los gastos del grupo
     */
    public List<Balance> calcular(List<UUID> miembrosIds, List<Gasto> gastos) {
        throw new UnsupportedOperationException("TODO: implementar calculo de balances");
    }
}
