package com.gastoscompartidos.infrastructure.persistence.adapter;

import com.gastoscompartidos.application.port.out.UsuarioRepositoryPort;
import com.gastoscompartidos.domain.model.Usuario;
import com.gastoscompartidos.infrastructure.persistence.mapper.UsuarioMapper;
import com.gastoscompartidos.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida: implementa {@link UsuarioRepositoryPort} sobre Spring Data JPA.
 *
 * <p>Aqui es donde se invierte la dependencia. Application define la interfaz;
 * infrastructure la implementa. En tiempo de compilacion la flecha apunta
 * infrastructure -> application, nunca al reves.
 */
@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository, UsuarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        // TODO: jpaRepository.findById(id).map(mapper::aDominio)
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        // TODO: jpaRepository.findByEmail(email).map(mapper::aDominio)
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public List<Usuario> buscarPorIds(List<UUID> ids) {
        // TODO: jpaRepository.findAllById(ids) + mapear
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // TODO: mapper.aEntidad -> jpaRepository.save -> mapper.aDominio
        throw new UnsupportedOperationException("TODO: implementar");
    }

    @Override
    public boolean existePorEmail(String email) {
        // TODO: jpaRepository.existsByEmail(email)
        throw new UnsupportedOperationException("TODO: implementar");
    }
}
