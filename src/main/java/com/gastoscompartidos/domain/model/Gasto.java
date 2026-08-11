package com.gastoscompartidos.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio y <b>Aggregate Root</b>: un gasto pagado por un miembro y repartido
 * entre varios.
 *
 * <p>Invariante central del agregado: la suma de las {@link DivisionGasto} debe ser
 * exactamente igual al importe total del gasto. Si no, el gasto no puede existir.
 */
public class Gasto {

    private final UUID id;
    private final UUID grupoId;
    /** Quien puso el dinero. */
    private final UUID pagadorId;
    private String descripcion;
    private Dinero importeTotal;
    private final TipoDivision tipoDivision;
    private final List<DivisionGasto> divisiones;
    private LocalDateTime fecha;

    public Gasto(UUID id,
                 UUID grupoId,
                 UUID pagadorId,
                 String descripcion,
                 Dinero importeTotal,
                 TipoDivision tipoDivision,
                 List<DivisionGasto> divisiones,
                 LocalDateTime fecha) {
        // TODO: validar invariantes y lanzar DivisionInvalidaException / DomainException:
        //       - ids y fecha no nulos
        //       - descripcion no vacia
        //       - importeTotal positivo
        //       - divisiones no vacias, sin usuarios duplicados
        //       - todas las divisiones en la misma moneda que importeTotal
        //       - SUMA(divisiones.importe) == importeTotal   <-- invariante clave
        this.id = id;
        this.grupoId = grupoId;
        this.pagadorId = pagadorId;
        this.descripcion = descripcion;
        this.importeTotal = importeTotal;
        this.tipoDivision = tipoDivision;
        this.divisiones = new ArrayList<>(divisiones == null ? List.of() : divisiones);
        this.fecha = fecha;
    }

    /**
     * TODO: factory de division EQUITATIVA.
     *       Usa Dinero.repartirEn(participantes.size()) para no perder centimos.
     */
    public static Gasto crearEquitativo(UUID grupoId,
                                        UUID pagadorId,
                                        String descripcion,
                                        Dinero importeTotal,
                                        List<UUID> participantesIds,
                                        LocalDateTime fecha) {
        throw new UnsupportedOperationException("TODO: implementar reparto equitativo");
    }

    /**
     * TODO: factory con importes explicitos. Valida que sumen el total.
     */
    public static Gasto crearConImportesExactos(UUID grupoId,
                                                UUID pagadorId,
                                                String descripcion,
                                                Dinero importeTotal,
                                                List<DivisionGasto> divisiones,
                                                LocalDateTime fecha) {
        throw new UnsupportedOperationException("TODO: implementar reparto por importes exactos");
    }

    /**
     * TODO: cuanto debe este usuario por este gasto (0 si no participa).
     *       Lo consume CalculadoraBalances.
     */
    public Dinero parteDe(UUID usuarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /**
     * TODO: true si el usuario aparece en alguna division.
     */
    public boolean participa(UUID usuarioId) {
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

    public String getDescripcion() {
        return descripcion;
    }

    public Dinero getImporteTotal() {
        return importeTotal;
    }

    public TipoDivision getTipoDivision() {
        return tipoDivision;
    }

    /** Copia inmutable: el aggregate protege su invariante de suma. */
    public List<DivisionGasto> getDivisiones() {
        return Collections.unmodifiableList(divisiones);
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Gasto otro)) {
            return false;
        }
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
