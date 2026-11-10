package gym_plus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gym_plus.model.enums.StatusTurma;
import java.util.List;
import gym_plus.model.enums.StatusMatricula;

@Entity 
public class Turma {

    @Id
    @GeneratedValue 
    private Long id;

    private String modalidade;
    private String horario;
    private int maximoVagas;

    @ManyToOne
    private Professor professor;

    @OneToMany(mappedBy = "turma")
    @JsonIgnoreProperties("turma")
    private List<Matricula> matriculas;

    @Enumerated(EnumType.STRING)
    private StatusTurma status;

    public Turma(){}

    public Turma(String modalidade, String horario, int maximoVagas, StatusTurma status) {
        this.modalidade = modalidade;
        this.horario = horario;
        this.maximoVagas = maximoVagas;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getMaximoVagas() {
        return maximoVagas;
    }

    public void setMaximoVagas(int maximoVagas) {
        this.maximoVagas = maximoVagas;
    }

    public StatusTurma getStatus() {
        return status;
    }

    public void setStatus(StatusTurma status) {
        this.status = status;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<Matricula> matriculas) {
        this.matriculas = matriculas;
    }

    /** Verifica disponibilidade considerando somente matrículas ativas. */
    public boolean verificarVagas() {
        return verificarVagas(contarMatriculasAtivas());
    }

    /** Sobrecarga usada quando a quantidade vem diretamente do banco de dados. */
    public boolean verificarVagas(int matriculasAtivas) {
        return maximoVagas > 0 && matriculasAtivas < maximoVagas;
    }

    public int contarMatriculasAtivas() {
        if (matriculas == null) return 0;
        return (int) matriculas.stream()
            .filter(m -> m != null && m.getStatus() == StatusMatricula.ATIVA)
            .count();
    }

    public void associarProfessor(Professor professor) {
        this.professor = professor;
    }

    public boolean verificarModalidade(String modalidade) {
        return this.modalidade.equals(modalidade);
    }

    public void atualizarDados(Turma turma) {
        this.setModalidade(turma.getModalidade());
        this.setHorario(turma.getHorario());
        this.setMaximoVagas(turma.getMaximoVagas());
        this.setProfessor(turma.getProfessor());
    }

    public void inativar() {
        this.status = StatusTurma.INATIVA;
    }
}