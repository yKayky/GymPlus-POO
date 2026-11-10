package gym_plus.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
import gym_plus.model.enums.StatusMatricula;
import gym_plus.model.enums.StatusTurma;

class TurmaTest {
    @Test
    void turmaSemListaDeMatriculasDeveTerVagas() {
        Turma turma = new Turma("Musculação", "08:00", 10, StatusTurma.ATIVA);
        assertTrue(turma.verificarVagas());
        assertTrue(turma.contarMatriculasAtivas() == 0);
    }

    @Test
    void somenteMatriculasAtivasDevemOcuparVagas() {
        Turma turma = new Turma("Musculação", "08:00", 1, StatusTurma.ATIVA);
        Matricula ativa = new Matricula();
        ativa.setStatus(StatusMatricula.ATIVA);
        Matricula cancelada = new Matricula();
        cancelada.setStatus(StatusMatricula.CANCELADA);
        turma.setMatriculas(List.of(ativa, cancelada));
        assertFalse(turma.verificarVagas());
        assertTrue(turma.contarMatriculasAtivas() == 1);
    }
}
