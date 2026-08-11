package com.gastoscompartidos.infrastructure.persistence.adapter;

import com.gastoscompartidos.application.port.out.GrupoRepositoryPort;
import com.gastoscompartidos.domain.model.Grupo;
import com.gastoscompartidos.infrastructure.persistence.mapper.GrupoMapper;
import com.gastoscompartidos.infrastructure.persistence.repository.GrupoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida: implementa {@link GrupoRepositoryPort} sobre Spring Data JPA.
 */
@Component
public class GrupoRepositoryAdapter implements GrupoRepositoryPort {

    private final GrupoJpaRepository jpaRepository;
    private final GrupoMapper mapper;

    public GrupoRepositoryAdapter(GrupoJpaRepository jpaRepository, GrupoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Grupo> buscarPorId(UUID id) {
        // TODO: jpaRepository.findById(id).map(mapper::aDominio)
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public List<Grupo> buscarPorMiembro(UUID usuarioId) {
        // TODO: jpaRepository.findByMiembro(usuarioId) + mapear
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Grupo guardar(Grupo grupo) {
        // TODO: mapper.aEntidad -> jpaRepository.save -> mapper.aDominio
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public boolean esMiembro(UUID grupoId, UUID usuarioId) {
        // TODO: jpaRepository.existsMiembro(grupoId, usuarioId)
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
