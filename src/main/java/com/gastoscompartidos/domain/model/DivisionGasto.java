package com.gastoscompartidos.domain.model;

import java.util.UUID;

/**
 * Value Object: la parte de un {@link Gasto} que le corresponde pagar a un usuario.
 *
 * <p>No tiene identidad propia — vive y muere con su gasto. De ahi que sea un record.
 */
public record DivisionGasto(UUID usuarioId, Dinero importe) {

    // TODO: constructor compacto que valide:
    //       - usuarioId no nulo
    //       - importe no nulo y no negativo
}
