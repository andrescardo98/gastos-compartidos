package com.gastoscompartidos.domain.exception;

/**
 * Las divisiones de un gasto no cumplen la invariante del agregado: normalmente porque
 * no suman el importe total, o porque hay participantes repetidos / ajenos al grupo.
 *
 * <p>Se traduce a HTTP 422 en la capa REST (la peticion es sintacticamente valida pero
 * viola una regla de negocio).
 */
public class DivisionInvalidaException extends DomainException {

    public DivisionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
