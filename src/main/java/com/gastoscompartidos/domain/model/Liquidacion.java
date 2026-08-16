package com.gastoscompartidos.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio y <b>Aggregate Root</b>: un pago ya realizado entre dos miembros de
 * un grupo, que salda parte o toda una deuda entre ellos.
 *
 * <p>Clase pura: sin anotaciones de Spring, JPA, Jackson ni Lombok.
 *
 * <p>Es un <b>hecho historico</b>: una vez registrada no se modifica, por eso todos sus
 * campos son {@code final} y no hay metodos mutadores. Corregir un pago mal anotado es
 * borrarlo y registrar otro, no editarlo: asi el historico nunca miente sobre lo que se
 * dijo en su momento.
 *
 * <p>Aggregate aparte de {@link Grupo} y de {@link Gasto}: los referencia por id, igual que
 * hace Gasto, para no cargar el grupo entero al anotar un pago.
 *
 * <p>Nota de alcance: esta entidad todavia NO entra en el calculo de balances.
 * {@code CalculadoraBalances} solo recorre gastos; incorporar las liquidaciones (restarlas
 * de la deuda neta) es un cambio aparte que toca su firma y sus tests.
 *
 * <p>Identidad por {@code id}: dos pagos del mismo importe entre las mismas personas el
 * mismo dia son liquidaciones distintas.
 */
public class Liquidacion {

    private final UUID id;
    private final UUID grupoId;
    /** Quien entrega el dinero (el deudor que salda). */
    private final UUID pagadorId;
    /** Quien lo recibe (el acreedor). */
    private final UUID receptorId;
    private final Dinero importe;
    private final LocalDateTime fecha;

    public Liquidacion(UUID id,
                       UUID grupoId,
                       UUID pagadorId,
                       UUID receptorId,
                       Dinero importe,
                       LocalDateTime fecha) {
        // TODO: validar invariantes y lanzar LiquidacionInvalidaException / DomainException:
        //       - ids, importe y fecha no nulos
        //       - pagadorId != receptorId (nadie se paga a si mismo)  <-- invariante clave
        //       - importe estrictamente positivo (ni cero ni negativo): el sentido del pago
        //         lo marcan pagador/receptor, no el signo del importe
        //       - fecha no futura (decidir si se admite, y documentarlo aqui)
        this.id = id;
        this.grupoId = grupoId;
        this.pagadorId = pagadorId;
        this.receptorId = receptorId;
        this.importe = importe;
        this.fecha = fecha;
    }

    /**
     * TODO: factory de alta que genera el id.
     *
     * <p>La comprobacion de que pagador y receptor son miembros del grupo NO va aqui:
     * necesita el Grupo, que es otro aggregate. La hace el caso de uso, igual que
     * CrearGastoService valida contra Grupo.esMiembro antes de construir el Gasto.
     */
    public static Liquidacion registrar(UUID grupoId,
                                        UUID pagadorId,
                                        UUID receptorId,
                                        Dinero importe,
                                        LocalDateTime fecha) {
        throw new UnsupportedOperationException("TODO: implementar alta de liquidacion");
    }

    /**
     * TODO: true si el usuario es el pagador o el receptor.
     *       Lo consumira CalculadoraBalances cuando las liquidaciones entren en el calculo.
     */
    public boolean involucraA(UUID usuarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    public UUID getId() {
        return id;
    }

    public UUID getGrupoId() {
        return grupoId;
    }

    public UUID getPagadorId() {
        return pagadorId;
    }

    public UUID getReceptorId() {
        return receptorId;
    }

    public Dinero getImporte() {
        return importe;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Liquidacion otra)) {
            return false;
        }
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
