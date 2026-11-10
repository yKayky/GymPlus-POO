package gym_plus.controller;

import gym_plus.exception.ApiExceptionHandler;
import gym_plus.model.Matricula;
import gym_plus.model.Plano;
import gym_plus.model.enums.StatusMatricula;
import gym_plus.repository.AlunoRepository;
import gym_plus.repository.MatriculaRepository;
import gym_plus.repository.PlanoRepository;
import gym_plus.repository.TurmaRepository;
import gym_plus.service.MatriculaService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Testa o contrato HTTP e as regras reais, sem depender do banco Oracle ou de JWT. */
class MatriculaControllerTest {
    private MockMvc mvc;
    private MatriculaRepository repository;
    private Matricula matricula;

    @BeforeEach
    void configurar() {
        repository = mock(MatriculaRepository.class);
        Plano plano = new Plano();
        plano.setDuracao(3);
        matricula = new Matricula();
        ReflectionTestUtils.setField(matricula, "id", 7L);
        matricula.setPlano(plano);
        matricula.setStatus(StatusMatricula.ATIVA);
        matricula.setDataInicio(LocalDate.of(2026, 10, 1));
        matricula.setDataFim(LocalDate.of(2027, 1, 1));
        when(repository.findById(7L)).thenReturn(Optional.of(matricula));
        when(repository.findAll()).thenReturn(List.of(matricula));
        when(repository.save(any(Matricula.class))).thenAnswer(i -> i.getArgument(0));
        MatriculaService service = new MatriculaService(repository,
            mock(AlunoRepository.class), mock(PlanoRepository.class), mock(TurmaRepository.class));
        mvc = MockMvcBuilders.standaloneSetup(new MatriculaController(service))
            .setControllerAdvice(new ApiExceptionHandler())
            .addFilters(new CharacterEncodingFilter("UTF-8", true)).build();
    }

    @Test
    void listagemIncluiIdentificadorNecessarioParaAsAcoes() throws Exception {
        mvc.perform(get("/matriculas"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(7));
    }

    @Test
    void renovaPeloIdentificadorEEstendeVencimentoPelaDuracaoDoPlano() throws Exception {
        mvc.perform(patch("/matriculas/7/renovar"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7));
        assertEquals(LocalDate.of(2027, 4, 1), matricula.getDataFim());
        assertEquals(StatusMatricula.ATIVA, matricula.getStatus());
        verify(repository).save(matricula);
    }

    @Test
    void cancelaComMotivoEnviadoComoTextoPeloFrontend() throws Exception {
        mvc.perform(patch("/matriculas/7/cancelar")
                .contentType(MediaType.TEXT_PLAIN).content("Mudança de cidade"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.status").value("CANCELADA"))
            .andExpect(jsonPath("$.motivoCancelamento").value("Mudança de cidade"));
        verify(repository).save(matricula);
    }

    @Test
    void rejeitaCancelamentoSemMotivoSemSalvar() throws Exception {
        mvc.perform(patch("/matriculas/7/cancelar")
                .contentType(MediaType.TEXT_PLAIN).content("   "))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensagem").value("Motivo do cancelamento é obrigatório"));
        verify(repository, never()).save(any());
    }

    @Test
    void rejeitaRenovacaoDeMatriculaCancelada() throws Exception {
        matricula.setStatus(StatusMatricula.CANCELADA);
        mvc.perform(patch("/matriculas/7/renovar"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensagem").value("Matrícula não está ativa"));
        verify(repository, never()).save(any());
    }
}
