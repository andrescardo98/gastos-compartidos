package com.gastoscompartidos.domain.exception;

import java.util.UUID;

/** El grupo referenciado no existe. Se traduce a HTTP 404 en la capa REST. */
public class GrupoNoEncontradoException extends DomainException {

    public GrupoNoEncontradoException(UUID grupoId) {
        super("No existe el grupo con id " + grupoId);
    }
}
