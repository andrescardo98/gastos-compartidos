package com.gastoscompartidos.domain.exception;

/**
 * La liquidacion no cumple las invariantes de su agregado: normalmente porque pagador y
 * receptor son la misma persona, o porque el importe no es estrictamente positivo.
 *
 * <p>Se traduce a HTTP 422 en la capa REST, igual que {@link DivisionInvalidaException}:
 * la peticion es sintacticamente valida pero viola una regla de negocio.
 */
public class LiquidacionInvalidaException extends DomainException {

    public LiquidacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
