package com.gastoscompartidos.domain.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests de {@link CalculadoraBalances}.
 *
 * <p>JUnit 5 puro: sin {@code @SpringBootTest}, sin contexto, sin base de datos.
 * Que estos tests puedan ser asi es exactamente la ventaja que compra la arquitectura
 * hexagonal — si algun dia hiciera falta levantar Spring para probar una regla de
 * negocio, es senal de que la regla se ha escapado del dominio.
 *
 * <p>Todo {@code @Disabled} hasta que exista la implementacion, para que el build
 * este en verde desde el primer commit.
 */
@DisplayName("CalculadoraBalances")
class CalculadoraBalancesTest {

    @Nested
    @DisplayName("casos basicos")
    class CasosBasicos {

        @Test
        @Disabled("TODO: implementar CalculadoraBalances.calcular")
        @DisplayName("un grupo sin gastos deja a todos los miembros a cero")
        void grupoSinGastos() {
            // TODO: dado un grupo con 3 miembros y ninguna gasto,
            //       entonces los 3 aparecen con saldo 0 (no se omite a nadie).
        }

        @Test
        @Disabled("TODO: implementar CalculadoraBalances.calcular")
        @DisplayName("quien paga un gasto equitativo queda acreedor del resto")
        void pagadorQuedaAcreedor() {
            // TODO: A paga 30 EUR repartidos entre A, B y C.
            //       Entonces A = +20, B = -10, C = -10.
        }

        @Test
        @Disabled("TODO: implementar CalculadoraBalances.calcular")
        @DisplayName("un miembro que no participa en ningun gasto queda a cero")
        void miembroSinParticipacion() {
            // TODO: A paga 20 repartido entre A y B; C es miembro pero no participa.
            //       Entonces C = 0 y aparece en el resultado.
        }
    }

    @Nested
    @DisplayName("propiedades invariantes")
    class Propiedades {

        @Test
        @Disabled("TODO: implementar CalculadoraBalances.calcular")
        @DisplayName("la suma de todos los saldos es siempre exactamente cero")
        void sumaDeSaldosEsCero() {
            // TODO: con cualquier combinacion de gastos, SUMA(saldos) == 0.
            //       Es la mejor red de seguridad contra errores de redondeo:
            //       si al repartir 10 EUR entre 3 se pierde un centimo, este test lo caza.
        }

        @Test
        @Disabled("TODO: implementar CalculadoraBalances.calcular")
        @DisplayName("importes no divisibles no pierden centimos")
        void repartoConCentimosSobrantes() {
            // TODO: 10.00 EUR entre 3 -> partes de 3.34 / 3.33 / 3.33, que suman 10.00.
            //       Nunca 3.33 x 3 = 9.99.
        }
    }
}
