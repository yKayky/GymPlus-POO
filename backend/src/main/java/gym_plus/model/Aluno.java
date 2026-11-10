package gym_plus.model;

import gym_plus.model.enums.StatusAluno;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;


@Entity
public class Aluno extends Pessoa {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Informe a data de nascimento.")
    @PastOrPresent(message = "A data de nascimento não pode ser futura.")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private StatusAluno status;

    public Aluno() {}

    public Aluno(
        String nome,
        String cpf,
        String telefone,
        String email,
        LocalDate dataNascimento,
        StatusAluno status
    ) {
        super(nome, cpf, telefone, email);

        this.dataNascimento = dataNascimento;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public StatusAluno getStatus() {
        return status;
    }

    public void setStatus(StatusAluno status) {
        this.status = status;
    }

    public void inativar() {
        this.status = StatusAluno.INATIVO;
    }

    public void atualizarDados(Aluno aluno) {
        this.setNome(aluno.getNome());
        this.setCpf(aluno.getCpf());
        this.setTelefone(aluno.getTelefone());
        this.setEmail(aluno.getEmail());
        this.dataNascimento = aluno.dataNascimento;
    }
}
