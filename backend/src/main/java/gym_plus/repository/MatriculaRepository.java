package gym_plus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gym_plus.model.Matricula;
import gym_plus.model.enums.StatusMatricula;


public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    boolean existsByAlunoIdAndStatus(Long alunoId, StatusMatricula status);
    long countByTurmaIdAndStatus(Long turmaId, StatusMatricula status);
}
