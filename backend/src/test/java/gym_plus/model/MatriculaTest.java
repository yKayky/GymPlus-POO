package gym_plus.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MatriculaTest {
    @Test
    void sobrecargaCalculaVencimentoComDuracaoInformada() {
        Matricula matricula = new Matricula();
        assertEquals(LocalDate.of(2027, 2, 3),
            matricula.calcularVencimento(LocalDate.of(2026, 11, 3), 3));
    }

    @Test
    void naoAceitaDuracaoInvalida() {
        Matricula matricula = new Matricula();
        assertThrows(IllegalArgumentException.class,
            () -> matricula.calcularVencimento(LocalDate.now(), 0));
    }
}
