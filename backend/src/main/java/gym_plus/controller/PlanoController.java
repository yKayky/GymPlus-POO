package gym_plus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import gym_plus.model.Plano;
import gym_plus.service.PlanoService;
import java.util.List;


@RestController
@RequestMapping("/planos")
public class PlanoController {
    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Plano cadastrar(@RequestBody Plano plano) {
        return planoService.cadastrar(plano);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public List<Plano> listarTodos() {
        return planoService.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    public Plano buscarPorId(@PathVariable Long id) {
        return planoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Plano atualizar(@PathVariable Long id, @RequestBody Plano plano) {
        return planoService.atualizar(id, plano);
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMIN')")
    public void inativar(@PathVariable Long id) {
        planoService.inativar(id);
    }
}