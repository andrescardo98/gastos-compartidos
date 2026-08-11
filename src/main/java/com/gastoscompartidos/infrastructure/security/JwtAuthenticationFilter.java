package com.gastoscompartidos.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que lee el header {@code Authorization: Bearer <token>} y, si el token es
 * valido, deja el usuario autenticado en el SecurityContext.
 *
 * <p>Extiende {@link OncePerRequestFilter} para no ejecutarse varias veces por peticion
 * (forwards, error dispatch).
 *
 * <p><b>Estado actual: pass-through.</b> Deja pasar la peticion sin autenticar nada
 * para que la aplicacion arranque mientras JwtService esta sin implementar. Los
 * endpoints siguen protegidos por SecurityConfig: sin autenticacion en el contexto,
 * todo lo que no sea {@code /api/auth/**} responde 401.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * TODO: implementar.
     *
     * <ol>
     *   <li>Leer el header {@code HEADER}; si falta o no empieza por {@code PREFIJO},
     *       continuar la cadena sin autenticar (no lanzar: puede ser una ruta publica).</li>
     *   <li>Recortar el prefijo y validar con {@code jwtService.esValido(token)}.</li>
     *   <li>Si es valido, {@code jwtService.extraerUsuarioId(token)} y meter un
     *       {@code UsernamePasswordAuthenticationToken} en el SecurityContextHolder.</li>
     *   <li>Continuar siempre con {@code filterChain.doFilter(...)}.</li>
     * </ol>
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }
}
