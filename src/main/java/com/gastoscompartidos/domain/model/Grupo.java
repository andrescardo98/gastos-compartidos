package com.gastoscompartidos.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio y <b>Aggregate Root</b>: un grupo de personas que comparten gastos.
 *
 * <p>Toda operacion que afecte a la composicion del grupo pasa por aqui. Los gastos
 * referencian el grupo por id (aggregate aparte), para no cargar en memoria el historico
 * completo cada vez que se toca un miembro.
 */
public class Grupo {

    private final UUID id;
    private String nombre;
    private final List<UUID> miembrosIds;

    public Grupo(UUID id, String nombre, List<UUID> miembrosIds) {
        // TODO: validar invariantes:
        //       - id no nulo, nombre no vacio
        //       - al menos 1 miembro
        //       - sin miembros duplicados
        this.id = id;
        this.nombre = nombre;
        this.miembrosIds = new ArrayList<>(miembrosIds == null ? List.of() : miembrosIds);
    }

    /**
     * TODO: factory de creacion. El creador es automaticamente el primer miembro.
     */
    public static Grupo crear(String nombre, UUID creadorId) {
        throw new UnsupportedOperationException("TODO: implementar creacion de grupo");
    }

    /**
     * TODO: anadir miembro. Debe ser idempotente o lanzar DomainException si ya pertenece.
     */
    public void anadirMiembro(UUID usuarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /**
     * TODO: quitar miembro. Regla de negocio a decidir y documentar:
     *       no se puede expulsar a alguien con balance != 0 dentro del grupo.
     */
    public void quitarMiembro(UUID usuarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /**
     * TODO: usado por CrearGasto para rechazar gastos de/para no-miembros.
     */
    public boolean esMiembro(UUID usuarioId) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    /** Copia inmutable: el aggregate protege su propia coleccion. */
    public List<UUID> getMiembrosIds() {
        return Collections.unmodifiableList(miembrosIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Grupo otro)) {
            return false;
        }
        return Objects.equals(id, otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
