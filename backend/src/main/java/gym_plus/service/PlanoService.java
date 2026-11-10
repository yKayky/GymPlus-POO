package gym_plus.service;

import gym_plus.model.Plano;
import gym_plus.repository.PlanoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PlanoService {

    private final PlanoRepository planoRepository;

    public PlanoService(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    public Plano cadastrar(Plano plano) {
        if (!plano.validarPlano()) {
            throw new IllegalArgumentException("Plano inválido");
        }
        return planoRepository.save(plano);
    }

    public List<Plano> listarTodos() {
        return planoRepository.findAll();
    }

    public Plano buscarPorId(Long id) {
        return planoRepository
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Plano não encontrado")
            );
    }

    public Plano atualizar(Long id, Plano plano) {
        Plano planoExistente = buscarPorId(id);
        planoExistente.atualizarDados(plano.getNome(), plano.getDuracao(), plano.getValorMensal());
        if (!planoExistente.validarPlano()) {
            throw new IllegalArgumentException("Plano inválido");
        }
        return planoRepository.save(planoExistente);
    }

    public void inativar(Long id) {
        Plano planoExistente = buscarPorId(id);
        planoExistente.inativar();
        planoRepository.save(planoExistente);
    }
}
