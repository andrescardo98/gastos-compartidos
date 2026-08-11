package com.gastoscompartidos.infrastructure.persistence.repository;

import com.gastoscompartidos.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data. Detalle de infraestructura: NO es el puerto.
 * El puerto es {@code application.port.out.UsuarioRepositoryPort}, y quien los une
 * es {@code UsuarioRepositoryAdapter}.
 */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

    Optional<UsuarioJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
