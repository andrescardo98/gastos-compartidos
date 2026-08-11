package com.gastoscompartidos.infrastructure.rest.dto;

import com.gastoscompartidos.application.port.in.BalanceGrupoResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO de salida con los saldos de un grupo.
 *
 * <p>Convencion de signo (documentarla tambien en la API): saldo positivo = le deben
 * dinero al usuario; negativo = el usuario debe.
 */
public record BalanceResponse(
        UUID grupoId,
        String codigoMoneda,
        List<SaldoResponse> saldos
) {

    public record SaldoResponse(UUID usuarioId, String nombre, BigDecimal saldo) {
    }

    /** TODO: factory BalanceGrupoResult -> BalanceResponse. */
    public static BalanceResponse desde(BalanceGrupoResult result) {
        throw new UnsupportedOperationException("TODO: implementar mapeo result -> response");
    }
}
