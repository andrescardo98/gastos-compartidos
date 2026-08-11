package com.gastoscompartidos.infrastructure;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base de los tests de integracion: levanta un PostgreSQL real en Docker.
 *
 * <p>Solo los adaptadores de infraestructura deberian heredar de aqui. Dominio y
 * casos de uso se prueban sin contexto de Spring — si un test de negocio necesita
 * esta clase, algo se ha filtrado fuera del hexagono.
 *
 * <p>Detalles:
 * <ul>
 *   <li>{@code static} + {@code withReuse}: un unico contenedor para toda la suite,
 *       no uno por clase.</li>
 *   <li>{@code @ServiceConnection} (Spring Boot 3.1+) inyecta solo url/usuario/password.
 *       Sustituye al {@code @DynamicPropertySource} manual que se ve en tutoriales
 *       antiguos.</li>
 *   <li>La imagen se fija a la misma version mayor que docker-compose.yml (postgres 16)
 *       para no probar contra un motor distinto del de desarrollo.</li>
 * </ul>
 *
 * <p>Requiere Docker corriendo. Si no lo esta, estos tests fallan al arrancar —
 * es esperado.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("gastos_compartidos_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    // TODO: al implementar los adaptadores, crear tests que hereden de esta clase, p.ej.
    //       GastoRepositoryAdapterIT — guardar un Gasto y releerlo comprobando que las
    //       divisiones vuelven completas y que los BigDecimal conservan la escala.
}
