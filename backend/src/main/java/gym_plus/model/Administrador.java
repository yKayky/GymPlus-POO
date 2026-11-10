package gym_plus.model;

import gym_plus.model.enums.StatusUsuario;
import jakarta.persistence.Entity;

@Entity
public class Administrador extends Usuario {

    public Administrador() {}

    public Administrador(String nome, String cpf, String telefone, String email,
        String senha, StatusUsuario status) {
        super(nome, cpf, telefone, email, senha, status);
    }
    @Override
    public String getPerfilAcesso() {
        return "ADMIN";
    }
}
