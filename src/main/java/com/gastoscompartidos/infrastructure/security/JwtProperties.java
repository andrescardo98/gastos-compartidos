package com.gastoscompartidos.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion del JWT, leida del prefijo {@code jwt} de application.yml.
 *
 * @param secret          clave HMAC en Base64. Para HS256 debe tener al menos 256 bits
 *                        (32 bytes) o jjwt lanza WeakKeyException. En produccion se
 *                        inyecta por variable de entorno, nunca se versiona.
 * @param expiracionMin   minutos de validez del token
 * @param emisor          claim "iss"
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expiracionMin, String emisor) {
}
