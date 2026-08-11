package com.gastoscompartidos.infrastructure.rest.advice;

import com.gastoscompartidos.domain.exception.DivisionInvalidaException;
import com.gastoscompartidos.domain.exception.DomainException;
import com.gastoscompartidos.domain.exception.GrupoNoEncontradoException;
import com.gastoscompartidos.domain.exception.UsuarioNoEncontradoException;
import com.gastoscompartidos.infrastructure.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce excepciones de dominio a codigos HTTP.
 *
 * <p>Este es el unico sitio donde "no existe el grupo" se convierte en un 404. El
 * dominio lanza {@link GrupoNoEncontradoException} sin saber que existe HTTP; la
 * traduccion es responsabilidad del adaptador. Por eso este handler vive en
 * infrastructure y no en application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** TODO: 404 con ErrorResponse.de(404, "GRUPO_NO_ENCONTRADO", ex.getMessage()). */
    @ExceptionHandler(GrupoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarGrupoNoEncontrado(GrupoNoEncontradoException ex) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /** TODO: 404 con ErrorResponse.de(404, "USUARIO_NO_ENCONTRADO", ex.getMessage()). */
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /**
     * TODO: 422 Unprocessable Entity.
     * El JSON es sintacticamente correcto pero viola una regla de negocio
     * (las partes no suman el total) — por eso 422 y no 400.
     */
    @ExceptionHandler(DivisionInvalidaException.class)
    public ResponseEntity<ErrorResponse> manejarDivisionInvalida(DivisionInvalidaException ex) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /** TODO: 400 para el resto de violaciones de invariantes del dominio. */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> manejarDomainException(DomainException ex) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /**
     * TODO: 400 con la lista de campos invalidos en {@code detalles}.
     * Extraer de {@code ex.getBindingResult().getFieldErrors()}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    /**
     * TODO: 500 generico.
     * Importante: loguear la excepcion completa pero NO devolver el stacktrace
     * ni el mensaje interno al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarInesperada(Exception ex) {
        throw new UnsupportedOperationException("TODO: implementar");
    }

    // Referencia rapida de codigos usados en esta API:
    //   400 Bad Request          -> JSON invalido / invariante generica
    //   401 Unauthorized         -> sin token o token invalido
    //   403 Forbidden            -> autenticado pero no miembro del grupo
    //   404 Not Found            -> grupo o usuario inexistente
    //   422 Unprocessable Entity -> regla de negocio violada
    @SuppressWarnings("unused")
    private static final HttpStatus[] CODIGOS_DOCUMENTADOS = {
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN,
            HttpStatus.NOT_FOUND,
            HttpStatus.UNPROCESSABLE_ENTITY
    };
}
