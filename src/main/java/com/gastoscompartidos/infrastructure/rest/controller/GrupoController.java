package com.gastoscompartidos.infrastructure.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada para la gestion de grupos (crear grupo, invitar miembro,
 * listar mis grupos).
 *
 * <p><b>Sin endpoints todavia a proposito.</b> Los tres casos de uso del alcance
 * inicial son CrearGasto, ObtenerBalanceGrupo y ObtenerPagosSugeridos; no hay
 * puertos de entrada para grupos.
 *
 * <p>Antes de anadir metodos aqui hay que crear primero sus puertos en
 * {@code application.port.in} (p.ej. {@code CrearGrupoUseCase},
 * {@code AnadirMiembroUseCase}) y sus implementaciones en
 * {@code application.usecase}. Inyectar aqui un repositorio o un servicio concreto
 * para "ir rapido" es exactamente lo que rompe el hexagono.
 */
@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    // TODO: inyectar CrearGrupoUseCase / AnadirMiembroUseCase / ListarMisGruposUseCase
    //       cuando esos puertos existan.

    // TODO: POST   /api/grupos                    -> crear grupo
    // TODO: GET    /api/grupos                    -> grupos del usuario autenticado
    // TODO: POST   /api/grupos/{grupoId}/miembros -> anadir miembro
}
