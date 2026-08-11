package com.gastoscompartidos.domain.model;

import java.util.UUID;

/**
 * Value Object: una transferencia propuesta para saldar deudas.
 *
 * <p>Se lee como "{@code deudorId} debe transferir {@code importe} a {@code acreedorId}".
 */
public record PagoSugerido(UUID deudorId, UUID acreedorId, Dinero importe) {

    // TODO: constructor compacto que valide:
    //       - deudorId y acreedorId no nulos y DISTINTOS entre si
    //       - importe estrictamente positivo (un pago de 0 no se sugiere)
}
