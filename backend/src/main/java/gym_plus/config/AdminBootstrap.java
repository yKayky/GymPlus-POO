package gym_plus.config;

import gym_plus.model.Administrador;
import gym_plus.model.enums.StatusUsuario;
import gym_plus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private final UsuarioRepository repository; private final PasswordEncoder encoder;
    @Value("${app.admin.nome:}") private String nome;
    @Value("${app.admin.cpf:}") private String cpf;
    @Value("${app.admin.telefone:}") private String telefone;
    @Value("${app.admin.email:}") private String email;
    @Value("${app.admin.senha:}") private String senha;
    public AdminBootstrap(UsuarioRepository repository, PasswordEncoder encoder) { this.repository=repository; this.encoder=encoder; }
    @Override public void run(String... args) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(senha)) throw new IllegalStateException("Configure ADMIN_EMAIL e ADMIN_SENHA para inicializar o administrador.");
        if (repository.findByEmailIgnoreCase(email).isPresent()) return;
        if (!StringUtils.hasText(nome) || !StringUtils.hasText(cpf) || !StringUtils.hasText(telefone)) throw new IllegalStateException("Configure ADMIN_NOME, ADMIN_CPF e ADMIN_TELEFONE.");
        Administrador admin = new Administrador(nome, cpf, telefone, email.trim().toLowerCase(), encoder.encode(senha), StatusUsuario.ATIVO);
        repository.save(admin);
    }
}
