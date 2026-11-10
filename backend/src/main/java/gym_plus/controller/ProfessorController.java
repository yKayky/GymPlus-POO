package gym_plus.controller;

import gym_plus.service.ProfessorService;
import gym_plus.model.Professor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Professor cadastrar(@RequestBody Professor professor) {
        return professorService.cadastrar(professor);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public List<Professor> listarTodos() {
        return professorService.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Professor buscarPorId(@PathVariable Long id) {
        return professorService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Professor atualizar(@PathVariable Long id, @RequestBody Professor professor) {
        return professorService.atualizar(id, professor);
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public void inativar(@PathVariable Long id) {
        professorService.inativar(id);
    }
}
