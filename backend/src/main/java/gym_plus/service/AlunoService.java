package gym_plus.service;

import gym_plus.model.Aluno;
import gym_plus.repository.AlunoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public Aluno cadastrar(Aluno aluno) {
        if (alunoRepository.existsByCpf(aluno.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        return alunoRepository.save(aluno);
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("Aluno não encontrado")
        );
    }

    public Aluno atualizar(Long id, Aluno aluno) {
        Aluno alunoExistente = buscarPorId(id);

        if (alunoRepository.existsByCpfAndIdNot(aluno.getCpf(), id)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        alunoExistente.atualizarDados(aluno);
        return alunoRepository.save(alunoExistente);
    }

    public void inativar(Long id) {
        Aluno alunoExistente = buscarPorId(id);

        alunoExistente.inativar();
        alunoRepository.save(alunoExistente);
    }
}
