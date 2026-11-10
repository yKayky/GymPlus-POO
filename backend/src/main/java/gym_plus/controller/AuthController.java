package gym_plus.controller;

import gym_plus.dto.LoginRequest;
import gym_plus.dto.LoginResponse;
import gym_plus.model.Usuario;
import gym_plus.model.enums.StatusUsuario;
import gym_plus.repository.UsuarioRepository;
import gym_plus.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioRepository repository; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthController(UsuarioRepository repository, PasswordEncoder encoder, JwtService jwt) { this.repository=repository; this.encoder=encoder; this.jwt=jwt; }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = repository.findByEmailIgnoreCase(request.email().trim()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos."));
        if (usuario.getStatus() != StatusUsuario.ATIVO || !encoder.matches(request.senha(), usuario.getSenha())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos.");
        String tipo = usuario.getPerfilAcesso();
        if (!tipo.equals("ADMIN") && !tipo.equals("FUNCIONARIO")) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este usuário não possui acesso administrativo.");
        return new LoginResponse(jwt.gerar(usuario, tipo), tipo, usuario.getNome(), usuario.getEmail());
    }
}
