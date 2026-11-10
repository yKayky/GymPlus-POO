package gym_plus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import gym_plus.service.TurmaService;
import gym_plus.model.Turma;

@RestController
@RequestMapping("/turmas")
public class TurmaController {
    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Turma cadastrar(@RequestBody Turma turma) {
        return turmaService.cadastrar(turma);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public List<Turma> listarTodos() {
        return turmaService.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Turma buscarPorId(@PathVariable Long id) {
        return turmaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Turma atualizar(@PathVariable Long id, @RequestBody Turma turma) {
        return turmaService.atualizar(id, turma);
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMIN')")
    public void inativar(@PathVariable Long id) {
        turmaService.inativar(id);
    }

}
