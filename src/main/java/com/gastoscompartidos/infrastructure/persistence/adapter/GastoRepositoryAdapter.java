package com.gastoscompartidos.infrastructure.persistence.adapter;

import com.gastoscompartidos.application.port.out.GastoRepositoryPort;
import com.gastoscompartidos.domain.model.Gasto;
import com.gastoscompartidos.infrastructure.persistence.mapper.GastoMapper;
import com.gastoscompartidos.infrastructure.persistence.repository.GastoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida: implementa {@link GastoRepositoryPort} sobre Spring Data JPA.
 */
@Component
public class GastoRepositoryAdapter implements GastoRepositoryPort {

    private final GastoJpaRepository jpaRepository;
    private final GastoMapper mapper;

    public GastoRepositoryAdapter(GastoJpaRepository jpaRepository, GastoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Gasto> buscarPorId(UUID id) {
        // TODO: jpaRepository.findWithDivisionesById(id).map(mapper::aDominio)
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public List<Gasto> buscarPorGrupo(UUID grupoId) {
        // TODO: jpaRepository.findByGrupoId(grupoId) + mapear (ya viene con @EntityGraph)
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Gasto guardar(Gasto gasto) {
        // TODO: mapper.aEntidad -> jpaRepository.save -> mapper.aDominio
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public void eliminar(UUID gastoId) {
        // TODO: jpaRepository.deleteById(gastoId)
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
