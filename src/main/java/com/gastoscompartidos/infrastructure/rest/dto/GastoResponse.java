package com.gastoscompartidos.infrastructure.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de salida de la API para un gasto.
 *
 * <p>Se construye desde {@code GastoResult}. Mantenerlo aparte permite cambiar la
 * forma del JSON publico sin tocar el caso de uso.
 */
public record GastoResponse(
        UUID id,
        UUID grupoId,
        UUID pagadorId,
        String descripcion,
        BigDecimal importeTotal,
        String codigoMoneda,
        List<ParteResponse> divisiones,
        LocalDateTime fecha
) {

    public record ParteResponse(UUID usuarioId, BigDecimal importe) {
    }

    /** TODO: factory GastoResult -> GastoResponse. */
    public static GastoResponse desde(com.gastoscompartidos.application.port.in.GastoResult result) {
        throw new UnsupportedOperationException("TODO: implementar mapeo result -> response");
    }
}
