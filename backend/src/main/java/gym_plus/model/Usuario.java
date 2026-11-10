package gym_plus.model;

import gym_plus.model.enums.StatusUsuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Usuario extends Pessoa {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    private String senha;

    @Enumerated(EnumType.STRING)
    private StatusUsuario status;

    public Usuario() {}

    public Usuario(
        String nome,
        String cpf,
        String telefone,
        String email,
        String senha,
        StatusUsuario status
    ) {
        super(nome, cpf, telefone, email);
        this.senha = senha;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }

    /** Retorna o perfil concreto do usuário; sobrescrito pelas subclasses. */
    public String getPerfilAcesso() {
        return "USUARIO";
    }

    public void alterarSenha(String novaSenha) {
        this.senha = novaSenha;
    }

    public void atualizarDados(Usuario usuario) {
        this.setNome(usuario.getNome());
        this.setCpf(usuario.getCpf());
        this.setTelefone(usuario.getTelefone());
        this.setEmail(usuario.getEmail());
    }

    public void inativar() {
        this.status = StatusUsuario.INATIVO;
    }
}
