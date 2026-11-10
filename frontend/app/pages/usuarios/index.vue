<template>
  <div>
    <!-- Cabeçalho -->
    <div class="page-header">
      <div class="header-titles">
        <h1>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--primary);">
            <circle cx="12" cy="12" r="3"></circle>
            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
          </svg>
          Usuários
        </h1>
        <p>Cadastre e organize quem pode acessar o sistema.</p>
      </div>

      <div class="header-actions">
        <button class="btn btn-secondary btn-sm" :disabled="loading" @click="carregarUsuarios">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ spinning: loading }">
            <polyline points="23 4 23 10 17 10"></polyline>
            <polyline points="1 20 1 14 7 14"></polyline>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
          </svg>
          Atualizar
        </button>

        <button
          v-if="isAdmin"
          class="btn btn-primary btn-sm"
          @click="abrirModalCadastro"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          Adicionar usuário
        </button>
      </div>
    </div>

    <!-- Banner de Permissões: se for Funcionário, alerta que ações são restritas -->
    <div v-if="!isAdmin" class="banner-alert banner-info">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="16" x2="12" y2="12"></line>
        <line x1="12" y1="8" x2="12.01" y2="8"></line>
      </svg>
      <div>
        <strong>Modo de Leitura Operacional (Funcionário)</strong>
        <p style="margin-top: 0.25rem;">
          Você está navegando com o perfil de Funcionário. Conforme as regras de negócio do sistema, operações de criação de novos usuários, alteração de senhas e inativação de contas são reservadas a usuários com permissão de Administrador. Peça ao administrador da academia para fazer essas alterações.
        </p>
      </div>
    </div>

    <!-- Alerta de Erro Geral -->
    <div v-if="erroGeral" class="banner-alert banner-danger">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="8" x2="12" y2="12"></line>
        <line x1="12" y1="16" x2="12.01" y2="16"></line>
      </svg>
      <div style="flex: 1;">
        <strong>Não foi possível carregar os usuários:</strong>
        <p style="margin-top: 0.25rem;">{{ erroGeral }}</p>
      </div>
      <button class="btn btn-sm btn-outline" style="border-color: currentColor; color: inherit;" @click="carregarUsuarios">
        Tentar de novo
      </button>
    </div>

    <!-- Tabela de Usuários -->
    <div class="gym-table-wrapper">
      <div class="table-toolbar">
        <div class="search-box">
          <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          <input
            v-model="filtroBusca"
            type="text"
            placeholder="Buscar por nome, CPF ou email..."
          />
        </div>

        <div class="filter-group">
          <label style="font-size: 0.85rem; font-weight: 600; color: var(--text-secondary);">Situação:</label>
          <select v-model="filtroSituação" class="form-select" style="width: auto; padding: 0.5rem 0.85rem; font-size: 0.85rem;">
            <option value="TODOS">Todas</option>
            <option value="ATIVO">Ativos</option>
            <option value="INATIVO">Inativos</option>
          </select>
        </div>
      </div>

      <!-- Carregamento -->
      <LoadingSpinner v-if="loading && usuariosList.length === 0" text="Carregando usuários do sistema..." />

      <!-- Vazio -->
      <EmptyState
        v-else-if="usuariosFiltrados.length === 0"
        title="Nenhum usuário cadastrado"
        message="Não há usuários retornados pela API ou compatíveis com os filtros."
      >
        <template #action>
          <button v-if="isAdmin" class="btn btn-primary btn-sm" @click="abrirModalCadastro">
            Cadastrar Primeiro Usuário
          </button>
        </template>
      </EmptyState>

      <!-- Lista de Usuários -->
      <div v-else class="table-responsive">
        <table class="gym-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>E-mail</th>
              <th>CPF</th>
              <th>Telefone</th>
              <th>Situação</th>
              <th style="text-align: right;">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in usuariosFiltrados" :key="user.id">
              <td style="font-family: monospace; font-size: 0.85rem; color: var(--text-muted);">#{{ user.id }}</td>
              <td class="primary-cell">{{ user.nome }}</td>
              <td>{{ user.email }}</td>
              <td>{{ formatarCpf(user.cpf) }}</td>
              <td>{{ user.telefone || '-' }}</td>
              <td>
                <SituaçãoBadge :status="user.status" />
              </td>
              <td class="actions-cell">
                <div class="row-actions">
                  <!-- Ações disponíveis quando perfil for ADMIN -->
                  <template v-if="isAdmin">
                    <button
                      class="btn btn-secondary btn-sm"
                      title="Editar dados cadastrais"
                      @click="abrirModalEdicao(user)"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                      </svg>
                      Editar
                    </button>

                    <button
                      class="btn btn-outline btn-sm"
                      title="Alterar senha de acesso"
                      @click="abrirModalSenha(user)"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <rect width="18" height="11" x="3" y="11" rx="2" ry="2"></rect>
                        <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                      </svg>
                      Senha
                    </button>

                    <button
                      v-if="user.status === 'ATIVO'"
                      class="btn btn-danger-outline btn-sm"
                      title="Desativar usuário"
                      @click="solicitarInativacao(user)"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line>
                      </svg>
                      Desativar
                    </button>
                  </template>

                  <!-- Para Funcionário: exibe apenas indicativo de somente leitura -->
                  <span v-else style="font-size: 0.8rem; color: var(--text-muted); font-style: italic;">
                    Acesso restrito
                  </span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-pagination-footer">
        <span>Mostrando <strong>{{ usuariosFiltrados.length }}</strong> de <strong>{{ usuariosList.length }}</strong> usuários</span>
      </div>
    </div>

    <!-- Modal Form (Cadastro/Edição de Usuário) -->
    <BaseModal
      v-model="modalFormAberto"
      :title="modoEdicao ? 'Editar Usuário' : 'Adicionar usuário'"
      :subtitle="modoEdicao ? `Atualizando informações do usuário #${usuarioSelecionado?.id}` : 'Cadastre um novo usuário administrativo ou funcionário'"
    >
      <form @submit.prevent="salvarUsuario">
        <div class="form-grid">
          <!-- Nome -->
          <div class="form-group full-width">
            <label class="form-label">Nome <span class="required">*</span></label>
            <input
              v-model="formulario.nome"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.nome }"
              placeholder="Ex: Mariana Castro"
              required
            />
            <span v-if="errosForm.nome" class="form-error">{{ errosForm.nome }}</span>
          </div>

          <!-- CPF -->
          <div class="form-group">
            <label class="form-label">CPF (somente números) <span class="required">*</span></label>
            <input
              v-model="formulario.cpf"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.cpf }"
              placeholder="000.000.000-00"
              maxlength="14"
              required
            />
            <span v-if="errosForm.cpf" class="form-error">{{ errosForm.cpf }}</span>
          </div>

          <!-- Email -->
          <div class="form-group">
            <label class="form-label">E-mail <span class="required">*</span></label>
            <input
              v-model="formulario.email"
              type="email"
              class="form-control"
              :class="{ 'has-error': errosForm.email }"
              placeholder="usuario@gymplus.com"
              required
            />
            <span v-if="errosForm.email" class="form-error">{{ errosForm.email }}</span>
          </div>

          <!-- Telefone -->
          <div class="form-group">
            <label class="form-label">Telefone <span class="required">*</span></label>
            <input
              v-model="formulario.telefone"
              type="text"
              class="form-control"
              placeholder="(00) 00000-0000"
              required
            />
          </div>

          <!-- Senha (obrigatória no cadastro) -->
          <div v-if="!modoEdicao" class="form-group">
            <label class="form-label">Senha Inicial <span class="required">*</span></label>
            <input
              v-model="formulario.senha"
              type="password"
              class="form-control"
              :class="{ 'has-error': errosForm.senha }"
              placeholder="••••••••"
              required
            />
            <span v-if="errosForm.senha" class="form-error">{{ errosForm.senha }}</span>
          </div>
        </div>

        <div style="margin-top: 1.5rem; display: flex; justify-content: flex-end; gap: 0.75rem;">
          <button
            type="button"
            class="btn btn-secondary btn-sm"
            :disabled="salvando"
            @click="modalFormAberto = false"
          >
            Voltar
          </button>
          <button
            type="submit"
            class="btn btn-primary btn-sm"
            :disabled="salvando"
          >
            <span v-if="salvando" class="spinner" style="width: 14px; height: 14px; border-width: 2px;"></span>
            {{ modoEdicao ? 'Atualizar' : 'Cadastrar Usuário' }}
          </button>
        </div>
      </form>
    </BaseModal>

    <!-- Modal Alterar Senha -->
    <BaseModal
      v-model="modalSenhaAberto"
      title="Alterar Senha do Usuário"
      :subtitle="`Defina uma nova senha para '${usuarioSelecionado?.nome}'`"
      size="sm"
    >
      <form @submit.prevent="salvarNovaSenha">
        <div class="form-group">
          <label class="form-label">Nova Senha <span class="required">*</span></label>
          <input
            v-model="novaSenha"
            type="password"
            class="form-control"
            :class="{ 'has-error': erroSenha }"
            placeholder="Digite a nova senha..."
            required
          />
          <span v-if="erroSenha" class="form-error">{{ erroSenha }}</span>
        </div>

        <div style="margin-top: 1.5rem; display: flex; justify-content: flex-end; gap: 0.75rem;">
          <button
            type="button"
            class="btn btn-secondary btn-sm"
            :disabled="salvandoSenha"
            @click="modalSenhaAberto = false"
          >
            Voltar
          </button>
          <button
            type="submit"
            class="btn btn-primary btn-sm"
            :disabled="salvandoSenha"
          >
            <span v-if="salvandoSenha" class="spinner" style="width: 14px; height: 14px; border-width: 2px;"></span>
            Salvar Nova Senha
          </button>
        </div>
      </form>
    </BaseModal>

    <!-- Confirmação de Inativação -->
    <ConfirmModal
      v-model="modalDesativarAberto"
      title="Desativar Usuário"
      :message="`Tem certeza que deseja inativar o acesso de '${usuarioSelecionado?.nome}'? O usuário não conseguirá mais autenticar.`"
      confirm-button-text="Sim, Desativar"
      :loading="inativando"
      @confirm="executarInativacao"
      @cancel="usuarioSelecionado = null"
    />
  </div>
</template>

<script setup lang="ts">
import { useGymApi } from '~/composables/useGymApi';
import { useToast } from '~/composables/useToast';
import { useAuthRole } from '~/composables/useAuthRole';
import type { Usuario } from '~/types/api';
import BaseModal from '~/components/common/BaseModal.vue';
import ConfirmModal from '~/components/common/ConfirmModal.vue';
import SituaçãoBadge from '~/components/common/StatusBadge.vue';
import LoadingSpinner from '~/components/common/LoadingSpinner.vue';
import EmptyState from '~/components/common/EmptyState.vue';

const { usuarios } = useGymApi();
const toast = useToast();
const { isAdmin } = useAuthRole();

const loading = ref(false);
const salvando = ref(false);
const inativando = ref(false);
const salvandoSenha = ref(false);
const erroGeral = ref<string | null>(null);

const usuariosList = ref<Usuario[]>([]);
const filtroBusca = ref('');
const filtroSituação = ref<'TODOS' | 'ATIVO' | 'INATIVO'>('TODOS');

const modalFormAberto = ref(false);
const modalSenhaAberto = ref(false);
const modalDesativarAberto = ref(false);
const modoEdicao = ref(false);
const usuarioSelecionado = ref<Usuario | null>(null);

const formulario = reactive({
  nome: '',
  cpf: '',
  email: '',
  telefone: '',
  senha: ''
});

const errosForm = reactive({
  nome: '',
  cpf: '',
  email: '',
  senha: ''
});

const novaSenha = ref('');
const erroSenha = ref('');

const limparErros = () => {
  errosForm.nome = '';
  errosForm.cpf = '';
  errosForm.email = '';
  errosForm.senha = '';
  erroSenha.value = '';
};

const formatarCpf = (cpf?: string): string => {
  if (!cpf) return '-';
  const clean = cpf.replace(/\D/g, '');
  if (clean.length === 11) {
    return clean.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }
  return cpf;
};

const usuariosFiltrados = computed(() => {
  return usuariosList.value.filter(user => {
    if (filtroSituação.value !== 'TODOS' && user.status !== filtroSituação.value) {
      return false;
    }

    if (filtroBusca.value.trim()) {
      const q = filtroBusca.value.toLowerCase();
      const matchNome = user.nome?.toLowerCase().includes(q);
      const matchCpf = user.cpf?.toLowerCase().includes(q);
      const matchEmail = user.email?.toLowerCase().includes(q);
      return matchNome || matchCpf || matchEmail;
    }

    return true;
  });
});

const carregarUsuarios = async () => {
  loading.value = true;
  erroGeral.value = null;
  try {
    const data = await usuarios.listar();
    usuariosList.value = data || [];
  } catch (e: any) {
    erroGeral.value = e.message;
    toast.error(e.message, 'Erro ao carregar usuários');
  } finally {
    loading.value = false;
  }
};

const abrirModalCadastro = () => {
  modoEdicao.value = false;
  usuarioSelecionado.value = null;
  limparErros();

  formulario.nome = '';
  formulario.cpf = '';
  formulario.email = '';
  formulario.telefone = '';
  formulario.senha = '';

  modalFormAberto.value = true;
};

const abrirModalEdicao = (user: Usuario) => {
  modoEdicao.value = true;
  usuarioSelecionado.value = user;
  limparErros();

  formulario.nome = user.nome || '';
  formulario.cpf = user.cpf || '';
  formulario.email = user.email || '';
  formulario.telefone = user.telefone || '';
  formulario.senha = '';

  modalFormAberto.value = true;
};

const abrirModalSenha = (user: Usuario) => {
  usuarioSelecionado.value = user;
  novaSenha.value = '';
  erroSenha.value = '';
  modalSenhaAberto.value = true;
};

const validarFormulario = (): boolean => {
  limparErros();
  let valido = true;

  if (!formulario.nome.trim()) {
    errosForm.nome = 'Nome é obrigatório.';
    valido = false;
  }

  const cleanCpf = formulario.cpf.replace(/\D/g, '');
  if (!cleanCpf || cleanCpf.length !== 11) {
    errosForm.cpf = 'Informe um CPF válido com 11 dígitos.';
    valido = false;
  }

  if (!formulario.email.trim()) {
    errosForm.email = 'Email é obrigatório.';
    valido = false;
  }

  if (!formulario.telefone.trim()) {
    valido = false;
    toast.error('Telefone é obrigatório.', 'Confira os dados');
  }

  if (!modoEdicao.value && formulario.senha.length < 8) {
    errosForm.senha = 'A senha deve ter pelo menos 8 caracteres.';
    valido = false;
  }

  return valido;
};

const salvarUsuario = async () => {
  if (!validarFormulario()) return;

  salvando.value = true;
  const payload: Usuario = {
    nome: formulario.nome.trim(),
    cpf: formulario.cpf.replace(/\D/g, ''),
    email: formulario.email.trim().toLowerCase(),
    telefone: formulario.telefone.trim(),
    status: modoEdicao.value ? (usuarioSelecionado.value?.status || 'ATIVO') : 'ATIVO'
  };

  if (!modoEdicao.value) {
    payload.senha = formulario.senha;
  }

  try {
    if (modoEdicao.value && usuarioSelecionado.value?.id) {
      const atualizado = await usuarios.atualizar(usuarioSelecionado.value.id, payload);
      toast.success(`Usuário ${atualizado.nome} atualizado com sucesso!`);
    } else {
      const criado = await usuarios.cadastrar(payload);
      toast.success(`Usuário ${criado.nome} cadastrado com sucesso!`);
    }

    modalFormAberto.value = false;
    await carregarUsuarios();
  } catch (e: any) {
    const msg = e.message || 'Erro ao salvar usuário';
    if (msg.toLowerCase().includes('cpf')) {
      errosForm.cpf = msg;
    } else if (msg.toLowerCase().includes('email')) {
      errosForm.email = msg;
    } else {
      toast.error(msg, 'Falha na gravação');
    }
  } finally {
    salvando.value = false;
  }
};

const salvarNovaSenha = async () => {
  if (!novaSenha.value.trim()) {
    erroSenha.value = 'Nova senha é obrigatória.';
    return;
  }

  if (!usuarioSelecionado.value?.id) return;

  salvandoSenha.value = true;
  try {
    await usuarios.atualizarSenha(usuarioSelecionado.value.id, novaSenha.value.trim());
    toast.success('Senha atualizada com sucesso!');
    modalSenhaAberto.value = false;
    novaSenha.value = '';
  } catch (e: any) {
    toast.error(e.message, 'Erro ao alterar senha');
  } finally {
    salvandoSenha.value = false;
  }
};

const solicitarInativacao = (user: Usuario) => {
  usuarioSelecionado.value = user;
  modalDesativarAberto.value = true;
};

const executarInativacao = async () => {
  if (!usuarioSelecionado.value?.id) return;

  inativando.value = true;
  try {
    await usuarios.inativar(usuarioSelecionado.value.id);
    toast.success(`Usuário inativado com sucesso.`);
    modalDesativarAberto.value = false;
    usuarioSelecionado.value = null;
    await carregarUsuarios();
  } catch (e: any) {
    toast.error(e.message, 'Erro ao inativar usuário');
  } finally {
    inativando.value = false;
  }
};

onMounted(() => {
  carregarUsuarios();
});
</script>
