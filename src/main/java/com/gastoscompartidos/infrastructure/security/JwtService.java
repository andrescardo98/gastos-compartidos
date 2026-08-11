package com.gastoscompartidos.infrastructure.security;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Emision y verificacion de JWT con jjwt.
 *
 * <p>Vive entera en infrastructure: el dominio no sabe que existen los tokens. Si algun
 * caso de uso necesitase saber "quien esta autenticado", recibe el {@code UUID} del
 * usuario como parametro del command/query — nunca lee el SecurityContext.
 */
@Service
public class JwtService {

    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    /**
     * TODO: generar el token.
     *
     * <ul>
     *   <li>subject = usuarioId.toString()</li>
     *   <li>claim "email"</li>
     *   <li>issuer = properties.emisor(), issuedAt = ahora,
     *       expiration = ahora + properties.expiracionMin()</li>
     *   <li>firmar con {@code Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()))}</li>
     * </ul>
     */
    public String generarToken(UUID usuarioId, String email) {
        throw new UnsupportedOperationException("TODO: implementar generacion de JWT");
    }

    /**
     * TODO: extraer el id de usuario del subject.
     * Lanzar excepcion si la firma no valida o el token expiro.
     */
    public UUID extraerUsuarioId(String token) {
        throw new UnsupportedOperationException("TODO: implementar extraccion de subject");
    }

    /**
     * TODO: true si firma y fecha de expiracion son validas.
     *
     * <p>Con jjwt 0.12.x el parser se construye asi:
     * {@code Jwts.parser().verifyWith(key).build().parseSignedClaims(token)}.
     * Los metodos {@code parserBuilder()} / {@code setSigningKey()} son de 0.11.x
     * y estan eliminados en esta version.
     */
    public boolean esValido(String token) {
        throw new UnsupportedOperationException("TODO: implementar validacion de JWT");
    }

    /** TODO: segundos que le quedan de vida al token (para TokenResponse). */
    public long segundosHastaExpiracion(String token) {
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
