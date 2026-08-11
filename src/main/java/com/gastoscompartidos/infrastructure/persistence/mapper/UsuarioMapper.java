package com.gastoscompartidos.infrastructure.persistence.mapper;

import com.gastoscompartidos.domain.model.Usuario;
import com.gastoscompartidos.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio y la entidad JPA.
 *
 * <p>El mapeo se escribe a mano a proposito: es el punto donde se ve explicitamente
 * que dominio y persistencia son dos modelos distintos. Con MapStruct o similar se
 * ahorra codigo, pero se pierde ese limite de vista.
 */
@Component
public class UsuarioMapper {

    /** TODO: JPA -> dominio. Reconstruye el Usuario por su constructor. */
    public Usuario aDominio(UsuarioJpaEntity entity) {
        throw new UnsupportedOperationException("TODO: implementar mapeo JPA -> dominio");
    }

    /** TODO: dominio -> JPA. */
    public UsuarioJpaEntity aEntidad(Usuario usuario) {
        throw new UnsupportedOperationException("TODO: implementar mapeo dominio -> JPA");
    }
}
