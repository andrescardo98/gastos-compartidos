package com.gastoscompartidos.infrastructure.persistence.mapper;

import com.gastoscompartidos.domain.model.Gasto;
import com.gastoscompartidos.infrastructure.persistence.entity.GastoJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link Gasto} (dominio) y {@link GastoJpaEntity} (persistencia),
 * incluidas sus divisiones.
 */
@Component
public class GastoMapper {

    /**
     * TODO: JPA -> dominio.
     *
     * <p>Reconstruir el Dinero a partir de (importe, codigoMoneda) y mapear cada
     * DivisionGastoJpaEntity a un DivisionGasto, descartando su PK tecnica.
     *
     * <p>Cuidado: el constructor de Gasto revalida la invariante de que las partes
     * suman el total. Si hay datos historicos corruptos en BD, este mapeo peta al leer.
     * Es el comportamiento deseado: mejor fallar que devolver balances erroneos.
     */
    public Gasto aDominio(GastoJpaEntity entity) {
        throw new UnsupportedOperationException("TODO: implementar mapeo JPA -> dominio");
    }

    /**
     * TODO: dominio -> JPA.
     *
     * <p>En una actualizacion no reemplazar la lista de divisiones por una nueva
     * instancia: Hibernate rastrea la coleccion original. Limpiarla y volver a llenarla
     * ({@code clear()} + {@code addAll()}) para que orphanRemoval funcione.
     */
    public GastoJpaEntity aEntidad(Gasto gasto) {
        throw new UnsupportedOperationException("TODO: implementar mapeo dominio -> JPA");
    }
}
