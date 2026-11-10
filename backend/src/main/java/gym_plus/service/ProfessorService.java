package gym_plus.service;

import gym_plus.model.Professor;
import gym_plus.repository.ProfessorRepository;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public Professor cadastrar(Professor professor) {
        if (professorRepository.existsByCpf(professor.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (professorRepository.existsByCref(professor.getCref())) {
            throw new IllegalArgumentException("CREF já cadastrado");
        }
        return professorRepository.save(professor);
    }

    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }

    public Professor buscarPorId(Long id) {
        return professorRepository
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Professor não encontrado")
            );
    }

    public Professor atualizar(Long id, Professor professor) {
        Professor professorExistente = buscarPorId(id);
        if (professorRepository.existsByCpfAndIdNot(professor.getCpf(), id)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (professorRepository.existsByCrefAndIdNot(professor.getCref(), id)) {
            throw new IllegalArgumentException("CREF já cadastrado");
        }
        professorExistente.atualizarDados(professor);
        return professorRepository.save(professorExistente);
    }

    public void inativar(Long id) {
        Professor professorExistente = buscarPorId(id);
        professorExistente.inativar();
        professorRepository.save(professorExistente);
    }
}
