package com.gastoscompartidos.infrastructure.persistence.adapter;

import com.gastoscompartidos.application.port.out.LiquidacionRepositoryPort;
import com.gastoscompartidos.domain.model.Liquidacion;
import com.gastoscompartidos.infrastructure.persistence.mapper.LiquidacionMapper;
import com.gastoscompartidos.infrastructure.persistence.repository.LiquidacionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida: implementa {@link LiquidacionRepositoryPort} sobre Spring Data JPA.
 *
 * <p>Aqui es donde se invierte la dependencia. Application define la interfaz;
 * infrastructure la implementa. En tiempo de compilacion la flecha apunta
 * infrastructure -> application, nunca al reves.
 */
@Component
public class LiquidacionRepositoryAdapter implements LiquidacionRepositoryPort {

    private final LiquidacionJpaRepository jpaRepository;
    private final LiquidacionMapper mapper;

    public LiquidacionRepositoryAdapter(LiquidacionJpaRepository jpaRepository,
                                        LiquidacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Liquidacion> buscarPorId(UUID id) {
        // TODO: jpaRepository.findById(id).map(mapper::aDominio)
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public List<Liquidacion> buscarPorGrupo(UUID grupoId) {
        // TODO: jpaRepository.findByGrupoIdOrderByFechaDesc(grupoId) + mapear
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Liquidacion guardar(Liquidacion liquidacion) {
        // TODO: mapper.aEntidad -> jpaRepository.save -> mapper.aDominio
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public void eliminar(UUID liquidacionId) {
        // TODO: jpaRepository.deleteById(liquidacionId)
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
