package gym_plus.model;

import gym_plus.model.enums.StatusMatricula;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
public class Matricula {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    private StatusMatricula status;

    private String motivoCancelamento;

    @ManyToOne
    private Aluno aluno;

    @ManyToOne
    private Plano plano;

    @ManyToOne
    @JsonIgnoreProperties("matriculas")
    private Turma turma;

    public Matricula() {}

    public Long getId() {
        return id;
    }

    public Matricula(
        LocalDate dataInicio,
        LocalDate dataFim,
        StatusMatricula status,
        Aluno aluno,
        Plano plano,
        Turma turma
    ) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.aluno = aluno;
        this.plano = plano;
        this.turma = turma;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Plano getPlano() {
        return plano;
    }

    public void setPlano(Plano plano) {
        this.plano = plano;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public void realizar() {
        this.status = StatusMatricula.ATIVA;
        this.dataFim = calcularVencimento(dataInicio);
    }

    // Sobrecarga: calcula usando a duração cadastrada no plano.
    public LocalDate calcularVencimento(LocalDate data) {
        if (plano == null) throw new IllegalStateException("Matrícula sem plano associado.");
        return calcularVencimento(data, plano.getDuracao());
    }

    // Sobrecarga: permite informar explicitamente a quantidade de meses.
    public LocalDate calcularVencimento(LocalDate data, int duracaoMeses) {
        if (data == null) throw new IllegalArgumentException("Data de referência obrigatória.");
        if (duracaoMeses <= 0) throw new IllegalArgumentException("A duração deve ser maior que zero.");
        return data.plusMonths(duracaoMeses);
    }

    public void renovar() {
        if (dataFim == null) throw new IllegalStateException("Matrícula sem data de término.");
        this.dataFim = calcularVencimento(dataFim);
    }

    public void cancelar(String motivoCancelamento) {
        this.status = StatusMatricula.CANCELADA;
        this.motivoCancelamento = motivoCancelamento;
    }

    public boolean verificarSituacao() {
        return dataFim != null && !dataFim.isBefore(LocalDate.now())
            && status == StatusMatricula.ATIVA;
    }

    public boolean estaProximaDoVencimento() {
        return verificarSituacao() && !dataFim.isAfter(LocalDate.now().plusDays(7));
    }
}
