package gym_plus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dados necessários para criar uma conta de acesso do tipo Funcionário. */
public record FuncionarioRequest(
    @NotBlank(message = "Nome é obrigatório.") String nome,
    @NotBlank(message = "CPF é obrigatório.") String cpf,
    @NotBlank(message = "Telefone é obrigatório.") String telefone,
    @Email(message = "Informe um e-mail válido.") @NotBlank(message = "E-mail é obrigatório.") String email,
    @NotBlank(message = "Senha é obrigatória.") @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.") String senha
) {}
