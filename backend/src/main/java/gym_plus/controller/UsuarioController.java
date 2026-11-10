package gym_plus.controller;

import gym_plus.model.Usuario;
import gym_plus.dto.FuncionarioRequest;
import gym_plus.model.Funcionario;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import gym_plus.service.UsuarioService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/funcionarios")
    @PreAuthorize("hasRole('ADMIN')")
    public Funcionario cadastrar(@Valid @RequestBody FuncionarioRequest request) {
        return usuarioService.cadastrarFuncionario(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Usuario atualizar(
        @PathVariable Long id,
        @RequestBody Usuario usuario
    ) {
        return usuarioService.atualizar(id, usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/senha")
    public Usuario atualizarSenha(
        @PathVariable Long id,
        @RequestBody String novaSenha
    ) {
        return usuarioService.atualizarSenha(id, novaSenha);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/inativar")
    public void inativar(@PathVariable Long id) {
        usuarioService.inativar(id);
    }
}
