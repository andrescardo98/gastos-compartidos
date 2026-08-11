package com.gastoscompartidos.infrastructure.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de entrada de la API REST.
 *
 * <p>Separado a proposito de {@code CrearGastoCommand}: aqui viven las anotaciones de
 * Bean Validation (formato/estructura del JSON), y en el command los datos que el caso
 * de uso necesita. La validacion de <b>negocio</b> — que las partes sumen el total —
 * no se hace aqui, sino en el dominio.
 *
 * <p>Nota: {@code grupoId} viene del path y {@code pagadorId} del token JWT,
 * no del body — por eso no aparecen en este DTO. Aceptar el pagador desde el body
 * dejaria que un usuario registrase gastos en nombre de otro.
 */
public record CrearGastoRequest(

        @NotBlank
        @Size(max = 255)
        String descripcion,

        @NotNull
        @DecimalMin(value = "0.01", message = "El importe debe ser mayor que cero")
        BigDecimal importeTotal,

        @NotBlank
        @Pattern(regexp = "[A-Z]{3}", message = "Codigo de moneda ISO-4217, p.ej. EUR")
        String codigoMoneda,

        @NotBlank
        String tipoDivision,

        List<UUID> participantesIds,

        @Valid
        List<ParteUsuarioRequest> importesPorUsuario,

        @NotNull
        LocalDateTime fecha
) {

    public record ParteUsuarioRequest(
            @NotNull UUID usuarioId,
            @NotNull BigDecimal valor
    ) {
    }
}
