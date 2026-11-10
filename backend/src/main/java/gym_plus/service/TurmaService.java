package gym_plus.service;

import gym_plus.model.Professor;
import gym_plus.model.Turma;
import gym_plus.model.enums.StatusProfessor;
import gym_plus.repository.ProfessorRepository;
import gym_plus.repository.TurmaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;

    public TurmaService(
        TurmaRepository turmaRepository,
        ProfessorRepository professorRepository
    ) {
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
    }

    public Turma cadastrar(Turma turma) {
        if (turma.getProfessor() == null) {
            throw new IllegalArgumentException("Turma precisa de um professor");
        }

        Professor professor = professorRepository
            .findById(turma.getProfessor().getId())
            .orElseThrow(() ->
                new IllegalArgumentException("Professor não encontrado")
            );

        if (professor.getStatus() == StatusProfessor.INATIVO) {
            throw new IllegalArgumentException("Professor inativo");
        }
        if (!professor.verificarModalidade(turma.getModalidade())) {
            throw new IllegalArgumentException(
                "Professor não corresponde à modalidade"
            );
        }
        if (turma.getMaximoVagas() <= 0) {
            throw new IllegalArgumentException(
                "Número máximo de vagas inválido"
            );
        }
        turma.setProfessor(professor);
        return turmaRepository.save(turma);
    }

    public List<Turma> listarTodos() {
        return turmaRepository.findAll();
    }

    public Turma buscarPorId(Long id) {
        return turmaRepository
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Turma não encontrada")
            );
    }

    public Turma atualizar(Long id, Turma turma) {
        Turma turmaExistente = buscarPorId(id);
        if (turma.getProfessor() == null) {
            throw new IllegalArgumentException("Turma precisa de um professor");
        }
        Professor professor = professorRepository
            .findById(turma.getProfessor().getId())
            .orElseThrow(() ->
                new IllegalArgumentException("Professor não encontrado")
            );
        if (professor.getStatus() == StatusProfessor.INATIVO) {
            throw new IllegalArgumentException("Professor inativo");
        }
        if (!professor.verificarModalidade(turma.getModalidade())) {
            throw new IllegalArgumentException(
                "Professor não corresponde à modalidade"
            );
        }
        if (turma.getMaximoVagas() <= 0) {
            throw new IllegalArgumentException(
                "Número máximo de vagas inválido"
            );
        }
        turma.setProfessor(professor);
        turmaExistente.atualizarDados(turma);
        return turmaRepository.save(turmaExistente);
    }

    public void inativar(Long id) {
        Turma turmaExistente = buscarPorId(id);
        turmaExistente.inativar();
        turmaRepository.save(turmaExistente);
    }
}
