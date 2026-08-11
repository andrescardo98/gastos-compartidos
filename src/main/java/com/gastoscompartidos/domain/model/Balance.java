package com.gastoscompartidos.domain.model;

import java.util.UUID;

/**
 * Value Object: el saldo neto de un usuario dentro de un grupo.
 *
 * <p>Convencion (fijarla aqui y no cambiarla nunca):
 * <ul>
 *   <li><b>saldo positivo</b> = al usuario le deben dinero (puso mas de lo que le tocaba)</li>
 *   <li><b>saldo negativo</b> = el usuario debe dinero</li>
 * </ul>
 *
 * <p>Propiedad que deben cumplir todos los balances de un grupo: la suma de los saldos
 * es siempre cero. Es la mejor asercion para los tests de CalculadoraBalances.
 */
public record Balance(UUID usuarioId, Dinero saldo) {

    // TODO: constructor compacto que valide usuarioId y saldo no nulos.

    /** TODO: true si saldo > 0 (acreedor). */
    public boolean esAcreedor() {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /** TODO: true si saldo < 0 (deudor). */
    public boolean esDeudor() {
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
