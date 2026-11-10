import type {
  Aluno,
  Professor,
  Usuario,
  Plano,
  Turma,
  Matricula,
  MatriculaRequest,
  ApiErrorResponse
} from '~/types/api';

export const useGymApi = () => {
  const config = useRuntimeConfig();
  const apiBase = (config.public.apiBase as string) || 'http://localhost:8080';

  /**
   * Extrai e formata a mensagem de erro vinda do backend Spring Boot
   */
  const extractErrorMessage = (err: any): string => {
    if (!err) return 'Ocorreu um erro inesperado.';

    // Falha de rede / Backend inacessível
    if (err.message && (err.message.includes('fetch failed') || err.message.includes('Failed to fetch') || err.message.includes('ECONNREFUSED'))) {
      return `Backend indisponível (${apiBase}). Verifique se a aplicação Spring Boot está em execução na porta configurada.`;
    }

    const data: ApiErrorResponse = err.data || {};

    // Erros de validação @Valid com campos específicos
    if (Array.isArray(data.errors) && data.errors.length > 0) {
      return data.errors.map(e => `${e.defaultMessage || e.field}`).join(' | ');
    }

    // Mensagem lançada pelo service (ex: IllegalArgumentException)
    if (data.message && data.message.trim().length > 0) {
      return data.message;
    }

    if (data.error && data.error.trim().length > 0) {
      return `${data.error} (Status ${data.status || err.statusCode || 500})`;
    }

    if (err.statusMessage) {
      return err.statusMessage;
    }

    return err.message || 'Falha na comunicação com o servidor.';
  };

  /**
   * Helper genérico para requisições usando $fetch
   */
  const request = async <T>(path: string, options: any = {}): Promise<T> => {
    const url = `${apiBase}${path}`;
    try {
      const token = useCookie<string | null>('gym_token');
      return await $fetch<T>(url, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          ...(token.value ? { Authorization: `Bearer ${token.value}` } : {}),
          ...(options.headers || {})
        }
      });
    } catch (err: any) {
      const friendlyMessage = extractErrorMessage(err);
      console.error(`[GYM+ API Error] ${options.method || 'GET'} ${url}:`, friendlyMessage, err);
      const enhancedError = new Error(friendlyMessage);
      (enhancedError as any).originalError = err;
      (enhancedError as any).status = err.statusCode || err.status;
      throw enhancedError;
    }
  };

  return {
    apiBase,
    extractErrorMessage,

    // Status da conexão
    health: {
      check: async (): Promise<boolean> => {
        try {
          await $fetch(`${apiBase}/planos`, { method: 'GET', timeout: 3000 });
          return true;
        } catch (e: any) {
          // Se respondeu status 4xx/5xx ainda assim o servidor está vivo
          if (e.statusCode || (e.response && e.response.status)) return true;
          return false;
        }
      }
    },

    // Módulo Alunos
    alunos: {
      listar: () => request<Aluno[]>('/alunos'),
      buscarPorId: (id: number) => request<Aluno>(`/alunos/${id}`),
      cadastrar: (aluno: Aluno) => request<Aluno>('/alunos', { method: 'POST', body: aluno }),
      atualizar: (id: number, aluno: Aluno) => request<Aluno>(`/alunos/${id}`, { method: 'PUT', body: aluno }),
      inativar: (id: number) => request<void>(`/alunos/${id}/inativar`, { method: 'PATCH' })
    },

    // Módulo Professores
    professores: {
      listar: () => request<Professor[]>('/professores'),
      buscarPorId: (id: number) => request<Professor>(`/professores/${id}`),
      cadastrar: (professor: Professor) => request<Professor>('/professores', { method: 'POST', body: professor }),
      atualizar: (id: number, professor: Professor) => request<Professor>(`/professores/${id}`, { method: 'PUT', body: professor }),
      inativar: (id: number) => request<void>(`/professores/${id}/inativar`, { method: 'PATCH' })
    },

    // Módulo Usuários e Funcionários
    usuarios: {
      listar: () => request<Usuario[]>('/usuarios'),
      buscarPorId: (id: number) => request<Usuario>(`/usuarios/${id}`),
      cadastrar: (funcionario: Pick<Usuario, 'nome' | 'cpf' | 'telefone' | 'email'> & { senha: string }) => request<Usuario>('/usuarios/funcionarios', { method: 'POST', body: funcionario }),
      atualizar: (id: number, usuario: Usuario) => request<Usuario>(`/usuarios/${id}`, { method: 'PUT', body: usuario }),
      atualizarSenha: (id: number, novaSenha: string) => {
        // Endpoint espera uma String simples no corpo
        return request<Usuario>(`/usuarios/${id}/senha`, {
          method: 'PATCH',
          body: novaSenha,
          headers: { 'Content-Type': 'text/plain' }
        });
      },
      inativar: (id: number) => request<void>(`/usuarios/${id}/inativar`, { method: 'PATCH' })
    },

    // Módulo Planos
    planos: {
      listar: () => request<Plano[]>('/planos'),
      buscarPorId: (id: number) => request<Plano>(`/planos/${id}`),
      cadastrar: (plano: Plano) => request<Plano>('/planos', { method: 'POST', body: plano }),
      atualizar: (id: number, plano: Plano) => request<Plano>(`/planos/${id}`, { method: 'PUT', body: plano }),
      inativar: (id: number) => request<void>(`/planos/${id}/inativar`, { method: 'PATCH' })
    },

    // Módulo Turmas
    turmas: {
      listar: () => request<Turma[]>('/turmas'),
      buscarPorId: (id: number) => request<Turma>(`/turmas/${id}`),
      cadastrar: (turma: Turma) => request<Turma>('/turmas', { method: 'POST', body: turma }),
      atualizar: (id: number, turma: Turma) => request<Turma>(`/turmas/${id}`, { method: 'PUT', body: turma }),
      inativar: (id: number) => request<void>(`/turmas/${id}/inativar`, { method: 'PATCH' })
    },

    // Módulo Matrículas
    matriculas: {
      listar: () => request<Matricula[]>('/matriculas'),
      buscarPorId: (id: number) => request<Matricula>(`/matriculas/${id}`),
      realizar: (payload: MatriculaRequest) => request<Matricula>('/matriculas', { method: 'POST', body: payload }),
      renovar: (id: number) => request<Matricula>(`/matriculas/${id}/renovar`, { method: 'PATCH' }),
      cancelar: (id: number, motivoCancelamento: string) => {
        // Backend espera @RequestBody String motivoCancelamento
        return request<Matricula>(`/matriculas/${id}/cancelar`, {
          method: 'PATCH',
          body: motivoCancelamento,
          headers: { 'Content-Type': 'text/plain' }
        });
      }
    }
  };
};
