package com.gastoscompartidos.domain.model;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Value Object: un importe monetario con su moneda.
 *
 * <p>Se modela como record porque es inmutable y su identidad es puramente el valor
 * (dos Dinero con mismo importe y moneda son intercambiables). Las entidades de negocio
 * (Usuario, Grupo, Gasto...) NO son records: tienen identidad propia e invariantes.
 *
 * <p>Regla: nunca usar double/float para dinero. Siempre BigDecimal.
 */
public record Dinero(BigDecimal importe, Currency moneda) {

    // TODO: constructor compacto que valide:
    //       - importe != null y moneda != null
    //       - importe con la escala de la moneda (moneda.getDefaultFractionDigits())
    //       - decidir si se permiten importes negativos (los balances si; los gastos no)

    /**
     * TODO: sumar dos importes. Debe lanzar DomainException si las monedas difieren.
     */
    public Dinero mas(Dinero otro) {
        throw new UnsupportedOperationException("TODO: implementar suma con validacion de moneda");
    }

    /**
     * TODO: restar. Misma validacion de moneda que {@link #mas(Dinero)}.
     */
    public Dinero menos(Dinero otro) {
        throw new UnsupportedOperationException("TODO: implementar resta con validacion de moneda");
    }

    /**
     * TODO: multiplicar por un factor (usado para divisiones por porcentaje).
     *       Redondeo HALF_UP a la escala de la moneda.
     */
    public Dinero por(BigDecimal factor) {
        throw new UnsupportedOperationException("TODO: implementar multiplicacion con redondeo");
    }

    /**
     * TODO: repartir este importe en n partes iguales, distribuyendo los centimos
     *       sobrantes de forma determinista para que la suma de las partes == total.
     *       Este es el metodo clave para la division EQUITATIVA de un gasto.
     */
    public java.util.List<Dinero> repartirEn(int partes) {
        throw new UnsupportedOperationException("TODO: implementar reparto sin perder centimos");
    }

    public boolean esNegativo() {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    public boolean esCero() {
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
