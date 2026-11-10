package gym_plus.controller;

import gym_plus.model.Matricula;
import gym_plus.service.MatriculaService;
import gym_plus.dto.MatriculaRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Matricula realizar(@RequestBody MatriculaRequest request) {
        return matriculaService.realizar(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public List<Matricula> listarTodos() {
        return matriculaService.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Matricula buscarPorId(@PathVariable Long id) {
        return matriculaService.buscarPorId(id);
    }

    @PatchMapping("/{id}/renovar")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Matricula renovar(@PathVariable Long id) {
        return matriculaService.renovar(id);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Matricula cancelar(@PathVariable Long id, @RequestBody String motivoCancelamento) {
        return matriculaService.cancelar(id, motivoCancelamento);
    }
}
