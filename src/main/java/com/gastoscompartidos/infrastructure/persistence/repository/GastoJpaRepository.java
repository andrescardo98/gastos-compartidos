package com.gastoscompartidos.infrastructure.persistence.repository;

import com.gastoscompartidos.infrastructure.persistence.entity.GastoJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data de gastos. Detalle de infraestructura, no un puerto.
 */
public interface GastoJpaRepository extends JpaRepository<GastoJpaEntity, UUID> {

    /**
     * @EntityGraph fuerza a traer las divisiones en la misma consulta. Sin esto,
     * CalculadoraBalances dispara un SELECT por gasto (N+1) al recorrer el grupo.
     */
    @EntityGraph(attributePaths = "divisiones")
    List<GastoJpaEntity> findByGrupoId(UUID grupoId);

    @EntityGraph(attributePaths = "divisiones")
    Optional<GastoJpaEntity> findWithDivisionesById(UUID id);
}
