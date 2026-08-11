package com.gastoscompartidos.application.port.in;

import java.util.UUID;

/**
 * Query de entrada del caso de uso {@link ObtenerPagosSugeridosUseCase}.
 *
 * @param grupoId       grupo a liquidar
 * @param solicitanteId quien pregunta; debe ser miembro del grupo
 */
public record ObtenerPagosSugeridosQuery(UUID grupoId, UUID solicitanteId) {

    // TODO: constructor compacto que valide que ambos ids son no nulos.
}
