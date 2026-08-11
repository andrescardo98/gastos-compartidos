package com.gastoscompartidos.domain.service;

import com.gastoscompartidos.domain.model.Balance;
import com.gastoscompartidos.domain.model.PagoSugerido;

import java.util.List;

/**
 * Domain Service: convierte una lista de balances en el minimo numero razonable de
 * transferencias que dejan a todo el mundo a cero.
 *
 * <p>Sin estado y sin dependencias, igual que {@link CalculadoraBalances}.
 *
 * <p><b>Nota sobre el algoritmo:</b> minimizar el numero de transferencias es
 * NP-hard en el caso general (se reduce a particion de conjuntos). La heuristica
 * habitual — greedy: emparejar repetidamente al mayor deudor con el mayor acreedor —
 * no siempre da el optimo absoluto, pero es O(n log n) y en grupos reales
 * produce como mucho n-1 transferencias, que es lo que la gente espera.
 * Documentar esta decision en un ADR cuando se implemente.
 */
public class SimplificadorDeudas {

    /**
     * TODO: implementar con la heuristica greedy.
     *
     * <p>Pasos:
     * <ol>
     *   <li>Separar deudores (saldo &lt; 0) y acreedores (saldo &gt; 0). Descartar los ceros.</li>
     *   <li>Meter ambos en colas de prioridad por valor absoluto descendente.</li>
     *   <li>Mientras queden: emparejar mayor deudor con mayor acreedor,
     *       transferir {@code min(|deuda|, credito)}, actualizar ambos saldos
     *       y reencolar el que no haya quedado a cero.</li>
     *   <li>Emitir un {@link PagoSugerido} por cada emparejamiento con importe &gt; 0.</li>
     * </ol>
     *
     * <p>Precondicion: la suma de los saldos de entrada debe ser cero. Si no lo es,
     * lanzar DomainException — significa que CalculadoraBalances tiene un bug.
     *
     * <p>Postcondicion util para tests: aplicar todos los pagos devueltos a los balances
     * de entrada deja todos los saldos a cero, y el resultado tiene como mucho n-1 pagos.
     */
    public List<PagoSugerido> simplificar(List<Balance> balances) {
        throw new UnsupportedOperationException("TODO: implementar simplificacion de deudas");
    }
}
