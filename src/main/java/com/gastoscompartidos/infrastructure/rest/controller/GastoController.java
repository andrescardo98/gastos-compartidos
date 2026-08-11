package com.gastoscompartidos.infrastructure.rest.controller;

import com.gastoscompartidos.application.port.in.CrearGastoUseCase;
import com.gastoscompartidos.infrastructure.rest.dto.CrearGastoRequest;
import com.gastoscompartidos.infrastructure.rest.dto.GastoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Adaptador de entrada (driving adapter) para gastos.
 *
 * <p>Depende de la <b>interfaz</b> {@link CrearGastoUseCase}, no de
 * {@code CrearGastoService}. Dos consecuencias practicas:
 * <ul>
 *   <li>en un {@code @WebMvcTest} se mockea el caso de uso entero con
 *       {@code @MockitoBean CrearGastoUseCase} y el test no toca ni dominio ni BD;</li>
 *   <li>se puede sustituir la implementacion sin recompilar el controller.</li>
 * </ul>
 *
 * <p>El controller no contiene logica de negocio: traduce HTTP -> command,
 * invoca el puerto y traduce result -> HTTP.
 */
@RestController
@RequestMapping("/api/grupos/{grupoId}/gastos")
public class GastoController {

    private final CrearGastoUseCase crearGastoUseCase;

    public GastoController(CrearGastoUseCase crearGastoUseCase) {
        this.crearGastoUseCase = crearGastoUseCase;
    }

    /**
     * TODO: implementar.
     *
     * <ol>
     *   <li>Sacar el id del usuario autenticado del principal (nunca del body).</li>
     *   <li>Construir el CrearGastoCommand con grupoId (path) + pagadorId (token) + request.</li>
     *   <li>{@code crearGastoUseCase.ejecutar(command)}.</li>
     *   <li>Devolver 201 Created con Location y el GastoResponse.</li>
     * </ol>
     */
    @PostMapping
    public ResponseEntity<GastoResponse> crear(@PathVariable UUID grupoId,
                                               @Valid @RequestBody CrearGastoRequest request,
                                               @AuthenticationPrincipal Object principal) {
        throw new UnsupportedOperationException("TODO: implementar endpoint de creacion de gasto");
    }
}
