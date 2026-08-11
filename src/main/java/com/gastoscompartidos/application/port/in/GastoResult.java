package com.gastoscompartidos.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de {@link CrearGastoUseCase}.
 *
 * <p>Se devuelve un result plano en vez del agregado {@code Gasto} para que la capa
 * REST no dependa del dominio y para poder cambiar el modelo interno sin romper la API.
 */
public record GastoResult(
        UUID gastoId,
        UUID grupoId,
        UUID pagadorId,
        String descripcion,
        BigDecimal importeTotal,
        String codigoMoneda,
        List<ParteCalculada> divisiones,
        LocalDateTime fecha
) {

    /** Lo que finalmente le toca pagar a cada participante, ya calculado por el dominio. */
    public record ParteCalculada(UUID usuarioId, BigDecimal importe) {
    }
}
