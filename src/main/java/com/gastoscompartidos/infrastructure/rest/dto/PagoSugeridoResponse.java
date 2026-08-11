package com.gastoscompartidos.infrastructure.rest.dto;

import com.gastoscompartidos.application.port.in.PagosSugeridosResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO de salida con las transferencias sugeridas para liquidar un grupo.
 */
public record PagoSugeridoResponse(
        UUID grupoId,
        String codigoMoneda,
        List<TransferenciaResponse> transferencias
) {

    public record TransferenciaResponse(
            UUID deudorId,
            String nombreDeudor,
            UUID acreedorId,
            String nombreAcreedor,
            BigDecimal importe
    ) {
    }

    /** TODO: factory PagosSugeridosResult -> PagoSugeridoResponse. */
    public static PagoSugeridoResponse desde(PagosSugeridosResult result) {
        throw new UnsupportedOperationException("TODO: implementar mapeo result -> response");
    }
}
