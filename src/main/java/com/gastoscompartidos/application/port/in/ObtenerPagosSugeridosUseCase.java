package com.gastoscompartidos.application.port.in;

/**
 * <b>Puerto de entrada</b> (driving port): obtener las transferencias sugeridas para
 * saldar las deudas de un grupo.
 *
 * <p>Implementacion: {@code application.usecase.ObtenerPagosSugeridosService}.
 */
public interface ObtenerPagosSugeridosUseCase {

    /**
     * @throws com.gastoscompartidos.domain.exception.GrupoNoEncontradoException si el grupo no existe
     */
    PagosSugeridosResult ejecutar(ObtenerPagosSugeridosQuery query);
}
