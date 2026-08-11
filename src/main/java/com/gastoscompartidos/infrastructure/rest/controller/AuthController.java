package com.gastoscompartidos.infrastructure.rest.controller;

import com.gastoscompartidos.infrastructure.rest.dto.LoginRequest;
import com.gastoscompartidos.infrastructure.rest.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada para autenticacion.
 *
 * <p>Caso especial dentro del hexagono: la autenticacion es una preocupacion tecnica,
 * no una regla de negocio de "gastos compartidos". Por eso vive entera en
 * infrastructure (controller + JwtService + UsuarioRepositoryPort) sin pasar por un
 * caso de uso de application. Si algun dia el registro necesitara reglas de negocio
 * (invitaciones, cuotas, verificacion), habria que promoverlo a un puerto de entrada.
 * Queda anotado como decision pendiente en docs/decisions/.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // TODO: inyectar AuthenticationManager, JwtService y UsuarioRepositoryPort

    /**
     * TODO: implementar.
     *
     * <ol>
     *   <li>Autenticar email + password con el AuthenticationManager.</li>
     *   <li>Si es valido, generar el JWT con JwtService (subject = id del usuario).</li>
     *   <li>Devolver 200 con TokenResponse.bearer(...).</li>
     *   <li>Si no, 401 — sin revelar si fallo el email o la contrasena.</li>
     * </ol>
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        throw new UnsupportedOperationException("TODO: implementar login");
    }

    // TODO: POST /api/auth/registro -> alta de usuario (hash con PasswordEncoder,
    //       comprobar unicidad de email con usuarioRepository.existePorEmail)
}
