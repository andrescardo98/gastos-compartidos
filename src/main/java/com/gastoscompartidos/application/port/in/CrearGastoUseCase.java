package com.gastoscompartidos.application.port.in;

/**
 * <b>Puerto de entrada</b> (driving port): registrar un gasto en un grupo.
 *
 * <p>Este es el contrato que la capa infrastructure ve del nucleo. Los controllers
 * dependen de esta interfaz, nunca de {@code CrearGastoService}, para poder mockear
 * el caso de uso completo en los tests de controller.
 *
 * <p>Implementacion: {@code application.usecase.CrearGastoService}.
 */
public interface CrearGastoUseCase {

    /**
     * Registra el gasto y devuelve el resultado con las divisiones ya calculadas.
     *
     * @throws com.gastoscompartidos.domain.exception.GrupoNoEncontradoException  si el grupo no existe
     * @throws com.gastoscompartidos.domain.exception.DivisionInvalidaException   si las partes no suman el total
     */
    GastoResult ejecutar(CrearGastoCommand command);
}
