package gym_plus.dto;

public class MatriculaRequest {
    private Long idAluno;
    private Long idPlano;
    private Long idTurma;

    public MatriculaRequest() {
    }

    public MatriculaRequest(Long idAluno, Long idPlano, Long idTurma) {
        this.idAluno = idAluno;
        this.idPlano = idPlano;
        this.idTurma = idTurma;
    }

    public Long getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
    }

    public Long getIdPlano() {
        return idPlano;
    }

    public void setIdPlano(Long idPlano) {
        this.idPlano = idPlano;
    }

    public Long getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(Long idTurma) {
        this.idTurma = idTurma;
    }
}
