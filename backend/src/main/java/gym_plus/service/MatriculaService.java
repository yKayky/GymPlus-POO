package gym_plus.service;

import gym_plus.dto.MatriculaRequest;
import gym_plus.model.Aluno;
import gym_plus.model.Matricula;
import gym_plus.model.Plano;
import gym_plus.model.Turma;
import gym_plus.model.enums.StatusAluno;
import gym_plus.model.enums.StatusMatricula;
import gym_plus.model.enums.StatusPlano;
import gym_plus.model.enums.StatusProfessor;
import gym_plus.model.enums.StatusTurma;
import gym_plus.repository.AlunoRepository;
import gym_plus.repository.MatriculaRepository;
import gym_plus.repository.PlanoRepository;
import gym_plus.repository.TurmaRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final TurmaRepository turmaRepository;

    public MatriculaService(
        MatriculaRepository matriculaRepository,
        AlunoRepository alunoRepository,
        PlanoRepository planoRepository,
        TurmaRepository turmaRepository
    ) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.turmaRepository = turmaRepository;
    }

    public Matricula realizar(MatriculaRequest request) {
        if (
            request.getIdAluno() == null ||
            request.getIdPlano() == null ||
            request.getIdTurma() == null
        ) {
            throw new IllegalArgumentException(
                "Aluno, plano e turma são obrigatórios"
            );
        }

        Aluno aluno = alunoRepository
            .findById(request.getIdAluno())
            .orElseThrow(() ->
                new IllegalArgumentException("Aluno não encontrado")
            );

        if (aluno.getStatus() == StatusAluno.INATIVO) {
            throw new IllegalArgumentException("Aluno inativo");
        }

        Plano plano = planoRepository
            .findById(request.getIdPlano())
            .orElseThrow(() ->
                new IllegalArgumentException("Plano não encontrado")
            );

        if (plano.getStatus() == StatusPlano.INATIVO) {
            throw new IllegalArgumentException("Plano inativo");
        }

        Turma turma = turmaRepository
            .findById(request.getIdTurma())
            .orElseThrow(() ->
                new IllegalArgumentException("Turma não encontrada")
            );

        if (turma.getStatus() == StatusTurma.INATIVA) {
            throw new IllegalArgumentException("Turma inativa");
        }

        if (turma.getProfessor() == null) {
            throw new IllegalArgumentException("Turma precisa de um professor");
        }

        if (turma.getProfessor().getStatus() == StatusProfessor.INATIVO) {
            throw new IllegalArgumentException("Professor inativo");
        }

        if (!turma.getProfessor().verificarModalidade(turma.getModalidade())) {
            throw new IllegalArgumentException(
                "Professor não corresponde à modalidade da turma"
            );
        }

        long matriculasAtivas = matriculaRepository.countByTurmaIdAndStatus(
            turma.getId(), StatusMatricula.ATIVA
        );
        if (!turma.verificarVagas((int) matriculasAtivas)) {
            throw new IllegalArgumentException("Turma sem vagas");
        }

        if (
            matriculaRepository.existsByAlunoIdAndStatus(
                aluno.getId(),
                StatusMatricula.ATIVA
            )
        ) {
            throw new IllegalArgumentException(
                "Aluno já possui uma matrícula ativa"
            );
        }

        Matricula matricula = new Matricula();

        if (matricula.getDataInicio() == null) {
            matricula.setDataInicio(LocalDate.now());
        }

        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setTurma(turma);

        matricula.realizar();

        return matriculaRepository.save(matricula);
    }

    public List<Matricula> listarTodos() {
        return matriculaRepository.findAll();
    }

    public Matricula buscarPorId(Long id) {
        return matriculaRepository
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Matrícula não encontrada")
            );
    }

    public Matricula renovar(Long id) {
        Matricula matricula = buscarPorId(id);

        if (matricula.getStatus() != StatusMatricula.ATIVA) {
            throw new IllegalArgumentException("Matrícula não está ativa");
        }

        matricula.renovar();

        return matriculaRepository.save(matricula);
    }

    public Matricula cancelar(Long id, String motivoCancelamento) {
        Matricula matricula = buscarPorId(id);

        if (matricula.getStatus() != StatusMatricula.ATIVA) {
            throw new IllegalArgumentException("Matrícula não está ativa");
        }

        if (motivoCancelamento == null || motivoCancelamento.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Motivo do cancelamento é obrigatório"
            );
        }

        matricula.cancelar(motivoCancelamento);

        return matriculaRepository.save(matricula);
    }
}
