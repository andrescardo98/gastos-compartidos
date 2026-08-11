package com.gastoscompartidos.application.port.in;

import com.gastoscompartidos.domain.model.TipoDivision;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Command de entrada del caso de uso {@link CrearGastoUseCase}.
 *
 * <p>Es un objeto de la capa application, no un DTO de REST: no lleva anotaciones de
 * Jackson ni de Bean Validation. El controller traduce su {@code CrearGastoRequest}
 * a este command. Asi el caso de uso se puede invocar igual desde REST, desde un
 * consumidor de mensajes o desde un test, sin arrastrar HTTP.
 *
 * @param grupoId          grupo al que pertenece el gasto
 * @param pagadorId        quien adelanto el dinero
 * @param descripcion      texto libre ("cena del sabado")
 * @param importeTotal     importe sin formato de moneda
 * @param codigoMoneda     ISO-4217, p.ej. "EUR"
 * @param tipoDivision     como repartir
 * @param participantesIds usados si tipoDivision == EQUITATIVA
 * @param importesPorUsuario usados si tipoDivision == IMPORTES_EXACTOS o PORCENTAJES
 * @param fecha            cuando ocurrio el gasto (no cuando se registro)
 */
public record CrearGastoCommand(
        UUID grupoId,
        UUID pagadorId,
        String descripcion,
        BigDecimal importeTotal,
        String codigoMoneda,
        TipoDivision tipoDivision,
        List<UUID> participantesIds,
        List<ParteUsuario> importesPorUsuario,
        LocalDateTime fecha
) {

    // TODO: constructor compacto con validacion estructural minima (no de negocio):
    //       - ids obligatorios no nulos
    //       - segun tipoDivision, exigir participantesIds o importesPorUsuario

    /**
     * Parte asignada a un usuario. {@code valor} es un importe en IMPORTES_EXACTOS
     * y un porcentaje (0-100) en PORCENTAJES.
     */
    public record ParteUsuario(UUID usuarioId, BigDecimal valor) {
    }
}
