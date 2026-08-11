package com.gastoscompartidos.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Resultado de {@link ObtenerBalanceGrupoUseCase}: el saldo neto de cada miembro.
 *
 * <p>Recordatorio de la convencion de signo del dominio:
 * saldo positivo = le deben; saldo negativo = debe.
 */
public record BalanceGrupoResult(
        UUID grupoId,
        String codigoMoneda,
        List<SaldoUsuario> saldos
) {

    public record SaldoUsuario(UUID usuarioId, String nombreUsuario, BigDecimal saldo) {
    }
}
