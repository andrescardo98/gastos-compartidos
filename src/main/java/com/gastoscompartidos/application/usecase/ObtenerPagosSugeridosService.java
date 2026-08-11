package com.gastoscompartidos.application.usecase;

import com.gastoscompartidos.application.port.in.ObtenerPagosSugeridosQuery;
import com.gastoscompartidos.application.port.in.ObtenerPagosSugeridosUseCase;
import com.gastoscompartidos.application.port.in.PagosSugeridosResult;
import com.gastoscompartidos.application.port.out.GastoRepositoryPort;
import com.gastoscompartidos.application.port.out.GrupoRepositoryPort;
import com.gastoscompartidos.application.port.out.UsuarioRepositoryPort;
import com.gastoscompartidos.domain.service.CalculadoraBalances;
import com.gastoscompartidos.domain.service.SimplificadorDeudas;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del puerto de entrada {@link ObtenerPagosSugeridosUseCase}.
 *
 * <p>Encadena los dos domain services: primero los balances, luego la simplificacion.
 */
@Service
public class ObtenerPagosSugeridosService implements ObtenerPagosSugeridosUseCase {

    private final GrupoRepositoryPort grupoRepository;
    private final GastoRepositoryPort gastoRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    private final CalculadoraBalances calculadoraBalances = new CalculadoraBalances();
    private final SimplificadorDeudas simplificadorDeudas = new SimplificadorDeudas();

    public ObtenerPagosSugeridosService(GrupoRepositoryPort grupoRepository,
                                        GastoRepositoryPort gastoRepository,
                                        UsuarioRepositoryPort usuarioRepository) {
        this.grupoRepository = grupoRepository;
        this.gastoRepository = gastoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * TODO: implementar la orquestacion.
     *
     * <ol>
     *   <li>Cargar grupo y verificar que el solicitante es miembro.</li>
     *   <li>Cargar los gastos del grupo.</li>
     *   <li>{@code balances = calculadoraBalances.calcular(miembros, gastos)}.</li>
     *   <li>{@code pagos = simplificadorDeudas.simplificar(balances)}.</li>
     *   <li>Resolver nombres y mapear a PagosSugeridosResult.</li>
     * </ol>
     *
     * <p>Nota: se recalcula desde cero en cada llamada. Es lo correcto de partida
     * (simple y siempre consistente). Si algun dia el volumen lo pide, cachear los
     * balances por grupo es un cambio local a esta capa.
     */
    @Override
    @Transactional(readOnly = true)
    public PagosSugeridosResult ejecutar(ObtenerPagosSugeridosQuery query) {
        throw new UnsupportedOperationException("TODO: implementar caso de uso ObtenerPagosSugeridos");
    }
}
