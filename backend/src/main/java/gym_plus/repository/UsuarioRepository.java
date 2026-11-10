package gym_plus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gym_plus.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    java.util.Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCpfAndIdNot(String cpf, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    
}