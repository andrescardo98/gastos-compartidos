package com.gastoscompartidos.infrastructure.persistence.mapper;

import com.gastoscompartidos.domain.model.Liquidacion;
import com.gastoscompartidos.infrastructure.persistence.entity.LiquidacionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link Liquidacion} (dominio) y {@link LiquidacionJpaEntity} (persistencia).
 *
 * <p>El mapeo se escribe a mano a proposito, como el resto: es el punto donde se ve que
 * dominio y persistencia son dos modelos distintos.
 */
@Component
public class LiquidacionMapper {

    /**
     * TODO: JPA -> dominio.
     *
     * <p>Reconstruir el Dinero a partir de (importe, codigoMoneda) con
     * {@code Currency.getInstance(codigoMoneda)}, y pasar por el constructor de
     * Liquidacion, que revalida las invariantes.
     *
     * <p>Cuidado: si en BD hubiera una fila con pagador == receptor o importe <= 0, este
     * mapeo peta al leer. Es el comportamiento deseado: mejor fallar que devolver saldos
     * calculados sobre datos imposibles.
     */
    public Liquidacion aDominio(LiquidacionJpaEntity entity) {
        throw new UnsupportedOperationException("TODO: implementar mapeo JPA -> dominio");
    }

    /**
     * TODO: dominio -> JPA.
     *
     * <p>Los seis campos del dominio se reparten en siete columnas: el Dinero se aplana en
     * importe + codigoMoneda ({@code moneda().getCurrencyCode()}). No perder ninguno de los
     * dos: un codigo de moneda que no se escribe convierte cualquier importe en ambiguo.
     */
    public LiquidacionJpaEntity aEntidad(Liquidacion liquidacion) {
        throw new UnsupportedOperationException("TODO: implementar mapeo dominio -> JPA");
    }
}
