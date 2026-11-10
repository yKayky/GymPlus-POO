package gym_plus.service;

import gym_plus.model.Usuario;
import gym_plus.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import gym_plus.model.Funcionario;
import gym_plus.model.enums.StatusUsuario;
import gym_plus.dto.FuncionarioRequest;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário obrigatório");
        }

        if (usuario.getNome() == null ||
            usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (usuario.getCpf() == null ||
            usuario.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }

        if (usuario.getEmail() == null ||
            usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (usuario.getSenha() == null ||
            usuario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }


    public Funcionario cadastrarFuncionario(FuncionarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados do funcionário são obrigatórios.");
        }

        String nome = request.nome() == null ? "" : request.nome().trim();
        String cpf = request.cpf() == null ? "" : request.cpf().replaceAll("\\D", "");
        String telefone = request.telefone() == null ? "" : request.telefone().trim();
        String email = request.email() == null ? "" : request.email().trim().toLowerCase();
        String senha = request.senha();

        if (nome.isBlank() || cpf.isBlank() || telefone.isBlank() || email.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios.");
        }
        if (senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        Funcionario funcionario = new Funcionario(
            nome, cpf, telefone, email, passwordEncoder.encode(senha), StatusUsuario.ATIVO
        );
        return usuarioRepository.save(funcionario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Usuário não encontrado")
            );
    }

    public Usuario atualizar(Long id, Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário obrigatório");
        }

        if (usuario.getNome() == null ||
            usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (usuario.getCpf() == null ||
            usuario.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }

        if (usuario.getEmail() == null ||
            usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        Usuario usuarioExistente = buscarPorId(id);

        if (usuarioRepository.existsByCpfAndIdNot(
                usuario.getCpf(), id)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(
                usuario.getEmail().trim(), id)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        usuarioExistente.atualizarDados(usuario);

        return usuarioRepository.save(usuarioExistente);
    }

    public Usuario atualizarSenha(Long id, String novaSenha) {

        if (novaSenha == null ||
            novaSenha.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Nova senha é obrigatória"
            );
        }

        Usuario usuarioExistente = buscarPorId(id);

        usuarioExistente.alterarSenha(passwordEncoder.encode(novaSenha));

        return usuarioRepository.save(usuarioExistente);
    }

    public void inativar(Long id) {

        Usuario usuarioExistente = buscarPorId(id);

        usuarioExistente.inativar();

        usuarioRepository.save(usuarioExistente);
    }
}
