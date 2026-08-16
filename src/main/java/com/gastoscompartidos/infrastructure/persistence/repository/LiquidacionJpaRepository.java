package com.gastoscompartidos.infrastructure.persistence.repository;

import com.gastoscompartidos.infrastructure.persistence.entity.LiquidacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data. Detalle de infraestructura: NO es el puerto.
 * El puerto es {@code application.port.out.LiquidacionRepositoryPort}, y quien los une
 * es {@code LiquidacionRepositoryAdapter}.
 *
 * <p>Sin {@code @EntityGraph}: Liquidacion no tiene colecciones hijas, asi que no hay N+1
 * que evitar aqui.
 */
public interface LiquidacionJpaRepository extends JpaRepository<LiquidacionJpaEntity, UUID> {

    /** El orden lo promete el puerto (mas recientes primero); se cumple aqui. */
    List<LiquidacionJpaEntity> findByGrupoIdOrderByFechaDesc(UUID grupoId);
}
