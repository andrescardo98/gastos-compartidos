package com.gastoscompartidos.application.port.in;

import java.util.UUID;

/**
 * Query de entrada del caso de uso {@link ObtenerBalanceGrupoUseCase}.
 *
 * @param grupoId    grupo del que se quieren los balances
 * @param solicitanteId quien pregunta; el caso de uso comprueba que sea miembro del grupo
 */
public record ObtenerBalanceGrupoQuery(UUID grupoId, UUID solicitanteId) {

    // TODO: constructor compacto que valide que ambos ids son no nulos.
}
