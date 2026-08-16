package com.gastoscompartidos.application.usecase;

import com.gastoscompartidos.application.port.out.GastoRepositoryPort;
import com.gastoscompartidos.application.port.out.GrupoRepositoryPort;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests del caso de uso {@link CrearGastoService} con Mockito.
 *
 * <p>Los mocks son los <b>puertos de salida</b>, no repositorios de Spring Data: el
 * caso de uso solo conoce las interfaces de {@code application.port.out}. Por eso este
 * test no necesita contexto de Spring ni base de datos.
 *
 * <p>Que se prueba aqui: la orquestacion (que se carga el grupo, que se rechaza si no
 * existe, que se persiste). El calculo del reparto se prueba en el dominio, no aqui:
 * duplicarlo solo produce tests fragiles.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CrearGastoService")
class CrearGastoServiceTest {

    @Mock
    private GrupoRepositoryPort grupoRepository;

    @Mock
    private GastoRepositoryPort gastoRepository;

    @InjectMocks
    private CrearGastoService crearGastoService;

    @Test
    @Disabled("TODO: implementar CrearGastoService.ejecutar")
    @DisplayName("lanza GrupoNoEncontradoException si el grupo no existe")
    void grupoInexistente() {
        // TODO: given grupoRepository.buscarPorId(...) devuelve Optional.empty()
        //       when  ejecutar(command)
        //       then  GrupoNoEncontradoException y NUNCA se llama a gastoRepository.guardar
    }

    @Test
    @Disabled("TODO: implementar CrearGastoService.ejecutar")
    @DisplayName("rechaza un gasto cuyo pagador no es miembro del grupo")
    void pagadorNoMiembro() {
        // TODO: then DivisionInvalidaException y verify(gastoRepository, never()).guardar(any())
    }

    @Test
    @Disabled("TODO: implementar CrearGastoService.ejecutar")
    @DisplayName("persiste el gasto y devuelve el result con las divisiones calculadas")
    void casoFeliz() {
        // TODO: given un grupo con 3 miembros
        //       when  ejecutar un command EQUITATIVA de 30 EUR
        //       then  se llama a gastoRepository.guardar una vez
        //             y el GastoResult trae 3 divisiones de 10 EUR
    }
}
