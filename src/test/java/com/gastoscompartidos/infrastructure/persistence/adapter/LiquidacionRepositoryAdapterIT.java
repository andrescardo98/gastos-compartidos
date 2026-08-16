package com.gastoscompartidos.infrastructure.persistence.adapter;

import com.gastoscompartidos.application.port.out.LiquidacionRepositoryPort;
import com.gastoscompartidos.infrastructure.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests de integracion de {@link LiquidacionRepositoryAdapter} contra PostgreSQL real.
 *
 * <p>Hereda de {@link AbstractIntegrationTest}, que ya aporta {@code @SpringBootTest},
 * {@code @Testcontainers} y el contenedor con {@code @ServiceConnection}. No repetir esas
 * anotaciones aqui.
 *
 * <p>Se inyecta el <b>puerto</b>, no el {@code LiquidacionJpaRepository}: lo que se prueba
 * es el contrato que ve application, no el detalle de Spring Data.
 *
 * <p>{@code @Disabled} a nivel de clase, igual que en los tests de dominio: mientras el
 * adaptador sea TODO no hay nada que verificar, y ademas evita que Testcontainers intente
 * levantar Docker en un build que solo compila. Ojo: hoy el pom no configura
 * maven-failsafe-plugin, asi que las clases {@code *IT} no las ejecuta ningun comando de
 * Maven todavia.
 */
@Disabled("TODO: habilitar al implementar LiquidacionRepositoryAdapter")
@DisplayName("LiquidacionRepositoryAdapter")
class LiquidacionRepositoryAdapterIT extends AbstractIntegrationTest {

    @Autowired
    private LiquidacionRepositoryPort puerto;

    @Test
    @DisplayName("guarda una liquidacion y la relee igual por id")
    void guardaYReleePorId() {
        // TODO: dada una Liquidacion registrada entre dos UUID distintos,
        //       cuando se guarda y se relee con buscarPorId,
        //       entonces vuelven pagador, receptor, fecha e importe identicos.
    }

    @Test
    @DisplayName("el importe conserva la escala al ida y vuelta")
    void conservaEscalaDelImporte() {
        // TODO: guardar 10.00 EUR y comprobar que vuelve con escala 2, no 10.0.
        //       Decidir explicitamente si se afirma con isEqualByComparingTo (valor)
        //       o con isEqualTo (valor + escala); aqui interesa la escala.
    }

    @Test
    @DisplayName("la moneda sobrevive al aplanado en dos columnas")
    void conservaLaMoneda() {
        // TODO: guardar un importe en una moneda no-EUR y comprobar que vuelve la misma.
        //       Caza el fallo clasico del mapper: escribir importe y olvidar codigoMoneda.
    }

    @Test
    @DisplayName("las liquidaciones de un grupo vuelven de la mas reciente a la mas antigua")
    void buscarPorGrupoRespetaElOrden() {
        // TODO: tres liquidaciones con fechas distintas en el mismo grupo,
        //       y una cuarta en otro grupo que NO debe aparecer.
    }
}
