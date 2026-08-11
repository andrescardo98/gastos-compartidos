package com.gastoscompartidos.domain.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests de {@link SimplificadorDeudas}. JUnit 5 puro, sin Spring.
 */
@DisplayName("SimplificadorDeudas")
class SimplificadorDeudasTest {

    @Test
    @Disabled("TODO: implementar SimplificadorDeudas.simplificar")
    @DisplayName("un grupo ya cuadrado no genera ninguna transferencia")
    void grupoCuadrado() {
        // TODO: todos los saldos a 0 -> lista vacia (no pagos de importe 0).
    }

    @Test
    @Disabled("TODO: implementar SimplificadorDeudas.simplificar")
    @DisplayName("un deudor y un acreedor generan una sola transferencia")
    void casoSimple() {
        // TODO: A = +10, B = -10  ->  un unico pago: B paga 10 a A.
    }

    @Test
    @Disabled("TODO: implementar SimplificadorDeudas.simplificar")
    @DisplayName("evita el triangulo: A->B->C se resuelve sin pasos intermedios")
    void evitaTriangulo() {
        // TODO: A = -10, B = 0, C = +10  ->  un unico pago A->C.
        //       B no debe aparecer aunque en el historico de gastos hubiera pasado por el.
        //       Este es el caso que justifica que exista este servicio.
    }

    @Test
    @Disabled("TODO: implementar SimplificadorDeudas.simplificar")
    @DisplayName("aplicar los pagos sugeridos deja todos los saldos a cero")
    void postcondicionSaldosACero() {
        // TODO: test de propiedad. Aplicar cada PagoSugerido a los balances de entrada
        //       y comprobar que todos quedan a 0. Comprobar tambien que el numero de
        //       pagos es <= n-1, con n = numero de personas con saldo != 0.
    }

    @Test
    @Disabled("TODO: implementar SimplificadorDeudas.simplificar")
    @DisplayName("rechaza balances cuya suma no es cero")
    void precondicionSumaCero() {
        // TODO: si SUMA(saldos) != 0, lanzar DomainException.
        //       Significa que CalculadoraBalances tiene un bug; fallar rapido.
    }
}
