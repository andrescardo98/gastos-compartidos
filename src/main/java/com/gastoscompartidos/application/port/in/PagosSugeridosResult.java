package com.gastoscompartidos.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de {@link ObtenerPagosSugeridosUseCase}: el conjunto minimo de
 * transferencias que deja el grupo a cero.
 */
public record PagosSugeridosResult(
        UUID grupoId,
        String codigoMoneda,
        List<Transferencia> transferencias
) {

    /** "{@code deudorId} paga {@code importe} a {@code acreedorId}". */
    public record Transferencia(
            UUID deudorId,
            String nombreDeudor,
            UUID acreedorId,
            String nombreAcreedor,
            BigDecimal importe
    ) {
    }
}
