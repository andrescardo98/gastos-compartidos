package com.gastoscompartidos.domain.exception;

import java.util.UUID;

/** El usuario referenciado no existe. Se traduce a HTTP 404 en la capa REST. */
public class UsuarioNoEncontradoException extends DomainException {

    public UsuarioNoEncontradoException(UUID usuarioId) {
        super("No existe el usuario con id " + usuarioId);
    }
}
