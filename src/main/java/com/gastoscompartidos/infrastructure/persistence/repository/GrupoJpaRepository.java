package com.gastoscompartidos.infrastructure.persistence.repository;

import com.gastoscompartidos.infrastructure.persistence.entity.GrupoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data de grupos. Detalle de infraestructura, no un puerto.
 */
public interface GrupoJpaRepository extends JpaRepository<GrupoJpaEntity, UUID> {

    // TODO: verificar la consulta contra la @ElementCollection al implementar el adaptador
    @Query("SELECT g FROM GrupoJpaEntity g JOIN g.miembrosIds m WHERE m = :usuarioId")
    List<GrupoJpaEntity> findByMiembro(@Param("usuarioId") UUID usuarioId);

    // TODO: comprobacion de pertenencia sin cargar el grupo entero
    @Query("""
            SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
            FROM GrupoJpaEntity g JOIN g.miembrosIds m
            WHERE g.id = :grupoId AND m = :usuarioId
            """)
    boolean existsMiembro(@Param("grupoId") UUID grupoId, @Param("usuarioId") UUID usuarioId);
}
