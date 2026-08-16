package com.gastoscompartidos.application.usecase;

import com.gastoscompartidos.application.port.in.BalanceGrupoResult;
import com.gastoscompartidos.application.port.in.ObtenerBalanceGrupoQuery;
import com.gastoscompartidos.application.port.in.ObtenerBalanceGrupoUseCase;
import com.gastoscompartidos.application.port.out.GastoRepositoryPort;
import com.gastoscompartidos.application.port.out.GrupoRepositoryPort;
import com.gastoscompartidos.application.port.out.UsuarioRepositoryPort;
import com.gastoscompartidos.domain.service.CalculadoraBalances;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del puerto de entrada {@link ObtenerBalanceGrupoUseCase}.
 */
@Service
public class ObtenerBalanceGrupoService implements ObtenerBalanceGrupoUseCase {

    private final GrupoRepositoryPort grupoRepository;
    private final GastoRepositoryPort gastoRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    /**
     * El domain service se instancia con {@code new}, no se inyecta: no tiene estado
     * ni dependencias, y asi el dominio no necesita ser un bean de Spring.
     */
    private final CalculadoraBalances calculadoraBalances = new CalculadoraBalances();

    public ObtenerBalanceGrupoService(GrupoRepositoryPort grupoRepository,
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
     *   <li>Cargar el grupo; si no existe, GrupoNoEncontradoException.</li>
     *   <li>Verificar que el solicitante es miembro (si no, lanzar la excepcion de
     *       autorizacion que se decida; ver TODO en GlobalExceptionHandler).</li>
     *   <li>Cargar los gastos con {@code gastoRepository.buscarPorGrupo(...)}.</li>
     *   <li>Delegar el calculo en {@code calculadoraBalances.calcular(miembros, gastos)}.</li>
     *   <li>Resolver los nombres con {@code usuarioRepository.buscarPorIds(...)}
     *       y mapear a BalanceGrupoResult.</li>
     * </ol>
     */
    @Override
    @Transactional(readOnly = true)
    public BalanceGrupoResult ejecutar(ObtenerBalanceGrupoQuery query) {
        throw new UnsupportedOperationException("TODO: implementar caso de uso ObtenerBalanceGrupo");
    }
}
