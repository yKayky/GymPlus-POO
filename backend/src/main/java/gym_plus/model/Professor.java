package gym_plus.model;

import gym_plus.model.enums.StatusProfessor;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Professor extends Pessoa {

    @Id
    @GeneratedValue
    private Long id;

    private String cref;
    private String modalidade;

    @Enumerated(EnumType.STRING)
    private StatusProfessor status;

    public Professor() {}

    public Professor(
        String nome,
        String cpf,
        String telefone,
        String email,
        String cref,
        String modalidade,
        StatusProfessor status
    ) {
        super(nome, cpf, telefone, email);

        this.cref = cref;
        this.modalidade = modalidade;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCref() {
        return cref;
    }

    public void setCref(String cref) {
        this.cref = cref;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public StatusProfessor getStatus() {
        return status;
    }

    public void setStatus(StatusProfessor status) {
        this.status = status;
    }

    public void inativar() {
        this.status = StatusProfessor.INATIVO;
    }

    public void atualizarDados(Professor professor) {
        this.setNome(professor.getNome());
        this.setEmail(professor.getEmail());
        this.setTelefone(professor.getTelefone());
        this.setCref(professor.getCref());
        this.setModalidade(professor.getModalidade());
    }

    public boolean verificarModalidade(String modalidade) {
        return this.modalidade.equals(modalidade);
    }
}
