package gym_plus.model;

import gym_plus.model.enums.StatusPlano;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Plano {

    @Id
    @GeneratedValue
    private Long id;

    private String nome;
    private int duracao;
    private double valorMensal;

    @Enumerated(EnumType.STRING)
    private StatusPlano status;

    public Plano() {}

    public Plano(
        String nome,
        int duracao,
        double valorMensal,
        StatusPlano status
    ) {
        this.nome = nome;
        this.duracao = duracao;
        this.valorMensal = valorMensal;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(double valorMensal) {
        this.valorMensal = valorMensal;
    }

    public StatusPlano getStatus() {
        return status;
    }

    public void setStatus(StatusPlano status) {
        this.status = status;
    }

    public void inativar() {
        this.status = StatusPlano.INATIVO;
    }

    public boolean validarPlano() {
        return (
            nome != null &&
            !nome.isBlank() &&
            duracao > 0 &&
            valorMensal > 0 &&
            status == StatusPlano.ATIVO
        );
    }

    public void atualizarDados(String nome, int duracao, double valorMensal) {
        this.nome = nome;
        this.duracao = duracao;
        this.valorMensal = valorMensal;
        
    }
}
