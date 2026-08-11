package com.gastoscompartidos.infrastructure.rest.dto;

import java.time.Instant;
import java.util.List;

/**
 * Cuerpo uniforme de error de la API.
 *
 * @param instante  cuando ocurrio
 * @param estado    codigo HTTP
 * @param error     nombre corto y estable ("DIVISION_INVALIDA")
 * @param mensaje   texto legible
 * @param detalles  errores de validacion campo a campo, si los hay
 */
public record ErrorResponse(
        Instant instante,
        int estado,
        String error,
        String mensaje,
        List<String> detalles
) {

    public static ErrorResponse de(int estado, String error, String mensaje) {
        return new ErrorResponse(Instant.now(), estado, error, mensaje, List.of());
    }
}
