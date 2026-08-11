package com.gastoscompartidos.domain.exception;

/**
 * Raiz de todas las excepciones de negocio.
 *
 * <p>Es unchecked a proposito: una violacion de invariante es un bug de uso, no un flujo
 * alternativo que cada llamante deba declarar. La traduccion a codigos HTTP la hace
 * {@code infrastructure.rest.advice.GlobalExceptionHandler} — el dominio no sabe que
 * existe HTTP.
 */
public class DomainException extends RuntimeException {

    public DomainException(String mensaje) {
        super(mensaje);
    }

    public DomainException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
