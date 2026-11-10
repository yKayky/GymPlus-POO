package gym_plus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gym_plus.model.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByCref(String cref);

    boolean existsByCpfAndIdNot(String cpf, Long id);
    boolean existsByCrefAndIdNot(String cref, Long id);
}