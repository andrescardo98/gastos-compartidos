package com.gastoscompartidos.domain.model;

/**
 * Como se reparte un gasto entre los participantes.
 */
public enum TipoDivision {

    /** Partes iguales entre todos los participantes. Reparte los centimos sobrantes. */
    EQUITATIVA,

    /** Cada participante aporta un importe explicito. Deben sumar el total del gasto. */
    IMPORTES_EXACTOS,

    /** Cada participante aporta un porcentaje. Deben sumar 100. */
    PORCENTAJES
}
