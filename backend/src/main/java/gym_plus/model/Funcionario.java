package gym_plus.model;

import gym_plus.model.enums.StatusUsuario;
import jakarta.persistence.Entity;

@Entity
public class Funcionario extends Usuario {

    public Funcionario() {}

    public Funcionario(String nome, String cpf, String telefone, String email,
        String senha, StatusUsuario status) {
        super(nome, cpf, telefone, email, senha, status);
    }
    @Override
    public String getPerfilAcesso() {
        return "FUNCIONARIO";
    }
}
