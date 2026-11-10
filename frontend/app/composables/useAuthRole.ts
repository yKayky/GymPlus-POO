import type { UserRole } from '~/types/api';

interface AuthUser {
  nome: string;
  email: string;
  tipo: UserRole;
}

interface LoginResponse {
  token: string;
  tipo: UserRole;
  nome: string;
  email: string;
}

/** Centraliza autenticação e perfil do usuário administrativo. */
export const useAuthRole = () => {
  const config = useRuntimeConfig();
  const token = useCookie<string | null>('gym_token', { sameSite: 'lax' });
  const user = useCookie<AuthUser | null>('gym_user', { sameSite: 'lax' });

  const currentUserName = computed(() => user.value?.nome ?? '');
  const currentRole = computed<UserRole | null>(() => user.value?.tipo ?? null);
  const isAdmin = computed(() => currentRole.value === 'ADMIN');
  const isFuncionario = computed(() => currentRole.value === 'FUNCIONARIO');
  const isAuthenticated = computed(() => Boolean(token.value && user.value));

  const login = async (email: string, senha: string) => {
    const response = await $fetch<LoginResponse>(`${config.public.apiBase}/auth/login`, {
      method: 'POST',
      body: { email, senha },
      headers: { 'Content-Type': 'application/json' }
    });
    token.value = response.token;
    user.value = { nome: response.nome, email: response.email, tipo: response.tipo };
    return response;
  };

  const logout = () => {
    token.value = null;
    user.value = null;
    return navigateTo('/login');
  };

  return { token, user, currentRole, currentUserName, isAdmin, isFuncionario, isAuthenticated, login, logout };
};
