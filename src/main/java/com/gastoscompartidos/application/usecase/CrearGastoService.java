package com.gastoscompartidos.application.usecase;

import com.gastoscompartidos.application.port.in.CrearGastoCommand;
import com.gastoscompartidos.application.port.in.CrearGastoUseCase;
import com.gastoscompartidos.application.port.in.GastoResult;
import com.gastoscompartidos.application.port.out.GastoRepositoryPort;
import com.gastoscompartidos.application.port.out.GrupoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del puerto de entrada {@link CrearGastoUseCase}.
 *
 * <p>Un caso de uso <b>orquesta, no calcula</b>: carga agregados por los puertos de
 * salida, delega la regla de negocio en el dominio y persiste el resultado. Si esta
 * clase empieza a acumular {@code if}s de negocio, ese codigo pertenece al dominio.
 *
 * <p>Aqui si se usan anotaciones de Spring ({@code @Service}, {@code @Transactional}):
 * application es la capa que define el limite transaccional. El dominio sigue limpio.
 */
@Service
public class CrearGastoService implements CrearGastoUseCase {

    private final GrupoRepositoryPort grupoRepository;
    private final GastoRepositoryPort gastoRepository;

    /** Inyeccion por constructor: sin {@code @Autowired}, Spring lo resuelve solo. */
    public CrearGastoService(GrupoRepositoryPort grupoRepository,
                             GastoRepositoryPort gastoRepository) {
        this.grupoRepository = grupoRepository;
        this.gastoRepository = gastoRepository;
    }

    /**
     * TODO: implementar la orquestacion.
     *
     * <ol>
     *   <li>Cargar el grupo con {@code grupoRepository.buscarPorId(...)};
     *       si no existe, GrupoNoEncontradoException.</li>
     *   <li>Comprobar que el pagador y todos los participantes son miembros
     *       ({@code grupo.esMiembro(...)}); si no, DivisionInvalidaException.</li>
     *   <li>Construir el Dinero del total a partir de importeTotal + codigoMoneda.</li>
     *   <li>Segun {@code command.tipoDivision()}, llamar a la factory correspondiente
     *       de Gasto (crearEquitativo / crearConImportesExactos / porcentajes).
     *       El dominio valida ahi la invariante de que las partes suman el total.</li>
     *   <li>Persistir con {@code gastoRepository.guardar(...)}.</li>
     *   <li>Mapear el Gasto resultante a GastoResult.</li>
     * </ol>
     */
    @Override
    @Transactional
    public GastoResult ejecutar(CrearGastoCommand command) {
        throw new UnsupportedOperationException("TODO: implementar caso de uso CrearGasto");
    }
}
