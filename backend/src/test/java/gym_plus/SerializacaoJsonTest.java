package gym_plus;

import tools.jackson.databind.json.JsonMapper;
import gym_plus.model.Aluno;
import gym_plus.model.Matricula;
import gym_plus.model.Plano;
import gym_plus.model.Professor;
import gym_plus.model.Turma;
import gym_plus.model.enums.StatusAluno;
import gym_plus.model.enums.StatusMatricula;
import gym_plus.model.enums.StatusPlano;
import gym_plus.model.enums.StatusProfessor;
import gym_plus.model.enums.StatusTurma;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SerializacaoJsonTest {

    @Test
    @DisplayName("Garante que a serialização JSON de Turma com matrículas não entra em recursão infinita")
    void testSerializacaoTurmaSemRecursaoInfinita() throws Exception {
        JsonMapper mapper = new JsonMapper();

        Professor professor = new Professor("Carlos", "12345678901", "18999999999", "carlos@gym.com", "123456-G/SP", "Musculação", StatusProfessor.ATIVO);
        Aluno aluno = new Aluno("João", "98765432100", "18988888888", "joao@email.com", LocalDate.of(2000, 1, 1), StatusAluno.ATIVO);
        Plano plano = new Plano("Mensal", 1, 100.0, StatusPlano.ATIVO);

        Turma turma = new Turma("Musculação", "08:00", 20, StatusTurma.ATIVA);
        turma.associarProfessor(professor);

        Matricula matricula = new Matricula(LocalDate.now(), LocalDate.now().plusMonths(1), StatusMatricula.ATIVA, aluno, plano, turma);

        List<Matricula> matriculas = new ArrayList<>();
        matriculas.add(matricula);
        turma.setMatriculas(matriculas);

        // Serializa Turma com sucesso
        String jsonTurma = mapper.writeValueAsString(turma);
        assertNotNull(jsonTurma);

        // Verifica que dados necessários da turma estão presentes
        assertTrue(jsonTurma.contains("\"modalidade\":\"Musculação\""));
        assertTrue(jsonTurma.contains("\"maximoVagas\":20"));
        assertTrue(jsonTurma.contains("\"matriculas\":["));

        // Verifica que dentro da lista de matriculas, o atributo turma foi omitido para evitar o ciclo
        assertFalse(jsonTurma.contains("\"turma\":{"));
    }

    @Test
    @DisplayName("Garante que a serialização JSON de Matrícula com turma não entra em recursão infinita")
    void testSerializacaoMatriculaSemRecursaoInfinita() throws Exception {
        JsonMapper mapper = new JsonMapper();

        Professor professor = new Professor("Carlos", "12345678901", "18999999999", "carlos@gym.com", "123456-G/SP", "Musculação", StatusProfessor.ATIVO);
        Aluno aluno = new Aluno("João", "98765432100", "18988888888", "joao@email.com", LocalDate.of(2000, 1, 1), StatusAluno.ATIVO);
        Plano plano = new Plano("Mensal", 1, 100.0, StatusPlano.ATIVO);

        Turma turma = new Turma("Musculação", "08:00", 20, StatusTurma.ATIVA);
        turma.associarProfessor(professor);

        Matricula matricula = new Matricula(LocalDate.now(), LocalDate.now().plusMonths(1), StatusMatricula.ATIVA, aluno, plano, turma);

        List<Matricula> matriculas = new ArrayList<>();
        matriculas.add(matricula);
        turma.setMatriculas(matriculas);

        // Serializa Matrícula com sucesso
        String jsonMatricula = mapper.writeValueAsString(matricula);
        assertNotNull(jsonMatricula);

        // Verifica que dados da turma necessária no frontend da matrícula estão presentes
        assertTrue(jsonMatricula.contains("\"turma\":{"));
        assertTrue(jsonMatricula.contains("\"modalidade\":\"Musculação\""));

        // Verifica que dentro da turma da matrícula, a lista de matrículas foi omitida para evitar o ciclo
        assertFalse(jsonMatricula.contains("\"matriculas\":["));
    }
}
