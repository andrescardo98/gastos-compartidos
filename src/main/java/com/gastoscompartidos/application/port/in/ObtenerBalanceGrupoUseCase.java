package com.gastoscompartidos.application.port.in;

/**
 * <b>Puerto de entrada</b> (driving port): consultar el saldo neto de cada miembro
 * de un grupo.
 *
 * <p>Implementacion: {@code application.usecase.ObtenerBalanceGrupoService}.
 */
public interface ObtenerBalanceGrupoUseCase {

    /**
     * @throws com.gastoscompartidos.domain.exception.GrupoNoEncontradoException si el grupo no existe
     */
    BalanceGrupoResult ejecutar(ObtenerBalanceGrupoQuery query);
}
