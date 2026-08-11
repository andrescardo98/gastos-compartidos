package com.gastoscompartidos.infrastructure.rest.controller;

import com.gastoscompartidos.application.port.in.ObtenerBalanceGrupoUseCase;
import com.gastoscompartidos.application.port.in.ObtenerPagosSugeridosUseCase;
import com.gastoscompartidos.infrastructure.rest.dto.BalanceResponse;
import com.gastoscompartidos.infrastructure.rest.dto.PagoSugeridoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Adaptador de entrada para las consultas de saldos y liquidacion.
 *
 * <p>Depende de las dos interfaces de {@code port.in}, no de sus implementaciones.
 */
@RestController
@RequestMapping("/api/grupos/{grupoId}")
public class BalanceController {

    private final ObtenerBalanceGrupoUseCase obtenerBalanceGrupoUseCase;
    private final ObtenerPagosSugeridosUseCase obtenerPagosSugeridosUseCase;

    public BalanceController(ObtenerBalanceGrupoUseCase obtenerBalanceGrupoUseCase,
                             ObtenerPagosSugeridosUseCase obtenerPagosSugeridosUseCase) {
        this.obtenerBalanceGrupoUseCase = obtenerBalanceGrupoUseCase;
        this.obtenerPagosSugeridosUseCase = obtenerPagosSugeridosUseCase;
    }

    /**
     * TODO: construir ObtenerBalanceGrupoQuery(grupoId, solicitanteId del token),
     *       invocar el puerto y devolver 200 con BalanceResponse.
     */
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> obtenerBalance(@PathVariable UUID grupoId,
                                                          @AuthenticationPrincipal Object principal) {
        throw new UnsupportedOperationException("TODO: implementar endpoint de balance");
    }

    /**
     * TODO: construir ObtenerPagosSugeridosQuery(grupoId, solicitanteId del token),
     *       invocar el puerto y devolver 200 con PagoSugeridoResponse.
     */
    @GetMapping("/pagos-sugeridos")
    public ResponseEntity<PagoSugeridoResponse> obtenerPagosSugeridos(@PathVariable UUID grupoId,
                                                                      @AuthenticationPrincipal Object principal) {
        throw new UnsupportedOperationException("TODO: implementar endpoint de pagos sugeridos");
    }
}
