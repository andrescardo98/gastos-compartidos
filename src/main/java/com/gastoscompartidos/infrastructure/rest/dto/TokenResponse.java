package com.gastoscompartidos.infrastructure.rest.dto;

/**
 * Respuesta del login.
 *
 * @param token       JWT firmado
 * @param tipo        siempre "Bearer"
 * @param expiraEnSeg segundos de validez restantes
 */
public record TokenResponse(String token, String tipo, long expiraEnSeg) {

    public static TokenResponse bearer(String token, long expiraEnSeg) {
        return new TokenResponse(token, "Bearer", expiraEnSeg);
    }
}
