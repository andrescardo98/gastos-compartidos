package com.gastoscompartidos.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad JPA de grupo.
 *
 * <p>Los miembros se guardan como {@code @ElementCollection} de UUIDs en la tabla
 * {@code grupo_miembros}, no como {@code @ManyToMany} a UsuarioJpaEntity: el agregado
 * Grupo del dominio solo conoce ids de miembros, no usuarios completos. Mantener el
 * mapeo igual de simple evita cargar usuarios enteros sin necesidad.
 */
@Entity
@Table(name = "grupos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "grupo_miembros",
            joinColumns = @JoinColumn(name = "grupo_id")
    )
    @Column(name = "usuario_id", nullable = false)
    private Set<UUID> miembrosIds = new LinkedHashSet<>();
}
