package com.gastoscompartidos.infrastructure.persistence.mapper;

import com.gastoscompartidos.domain.model.Grupo;
import com.gastoscompartidos.infrastructure.persistence.entity.GrupoJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link Grupo} (dominio) y {@link GrupoJpaEntity} (persistencia).
 */
@Component
public class GrupoMapper {

    /**
     * TODO: JPA -> dominio.
     * Ojo: la entidad guarda los miembros en un Set y el dominio los expone como List.
     */
    public Grupo aDominio(GrupoJpaEntity entity) {
        throw new UnsupportedOperationException("TODO: implementar mapeo JPA -> dominio");
    }

    /** TODO: dominio -> JPA. */
    public GrupoJpaEntity aEntidad(Grupo grupo) {
        throw new UnsupportedOperationException("TODO: implementar mapeo dominio -> JPA");
    }
}
