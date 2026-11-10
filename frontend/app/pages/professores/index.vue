<template>
  <div>
    <!-- Cabeçalho -->
    <div class="page-header">
      <div class="header-titles">
        <h1>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--primary);">
            <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
            <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
          </svg>
          Professores
        </h1>
        <p>Cadastre professores e informe o CREF e a modalidade.</p>
      </div>

      <div class="header-actions">
        <button class="btn btn-secondary btn-sm" :disabled="loading" @click="carregarProfessores">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ spinning: loading }">
            <polyline points="23 4 23 10 17 10"></polyline>
            <polyline points="1 20 1 14 7 14"></polyline>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
          </svg>
          Atualizar
        </button>

        <button class="btn btn-primary btn-sm" @click="abrirModalCadastro">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          Adicionar professor
        </button>
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
        <strong>Não foi possível carregar os professores:</strong>
        <p style="margin-top: 0.25rem;">{{ erroGeral }}</p>
      </div>
      <button class="btn btn-sm btn-outline" style="border-color: currentColor; color: inherit;" @click="carregarProfessores">
        Tentar de novo
      </button>
    </div>

    <!-- Tabela de Professores -->
    <div class="gym-table-wrapper">
      <!-- Toolbar -->
      <div class="table-toolbar">
        <div class="search-box">
          <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
          <input
            v-model="filtroBusca"
            type="text"
            placeholder="Buscar por nome, CREF ou modalidade..."
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

      <!-- Carregando -->
      <LoadingSpinner v-if="loading && professoresList.length === 0" text="Carregando professores..." />

      <!-- Vazio -->
      <EmptyState
        v-else-if="professoresFiltrados.length === 0"
        title="Nenhum professor encontrado"
        message="Não há professores cadastrados ou que atendam aos filtros de busca."
      >
        <template #action>
          <button class="btn btn-primary btn-sm" @click="abrirModalCadastro">
            Cadastrar Primeiro Professor
          </button>
        </template>
      </EmptyState>

      <!-- Lista -->
      <div v-else class="table-responsive">
        <table class="gym-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>CREF</th>
              <th>Modalidade</th>
              <th>CPF</th>
              <th>Telefone</th>
              <th>Situação</th>
              <th style="text-align: right;">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="prof in professoresFiltrados" :key="prof.id">
              <td style="font-family: monospace; font-size: 0.85rem; color: var(--text-muted);">#{{ prof.id }}</td>
              <td class="primary-cell">{{ prof.nome }}</td>
              <td>
                <span style="font-family: monospace; font-weight: 600; background: var(--bg-surface-alt); padding: 2px 6px; border-radius: 4px;">
                  {{ prof.cref }}
                </span>
              </td>
              <td>
                <strong style="color: var(--accent);">{{ prof.modalidade }}</strong>
              </td>
              <td>{{ formatarCpf(prof.cpf) }}</td>
              <td>
                <div style="font-size: 0.85rem;">
                  <div>{{ prof.email || '-' }}</div>
                  <div style="color: var(--text-muted); font-size: 0.775rem;">{{ prof.telefone || '-' }}</div>
                </div>
              </td>
              <td>
                <SituaçãoBadge :status="prof.status" />
              </td>
              <td class="actions-cell">
                <div class="row-actions">
                  <button
                    class="btn btn-secondary btn-sm"
                    title="Editar informações do professor"
                    @click="abrirModalEdicao(prof)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                    Editar
                  </button>

                  <button
                    v-if="prof.status === 'ATIVO'"
                    class="btn btn-danger-outline btn-sm"
                    title="Desativar este professor"
                    @click="solicitarInativacao(prof)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line>
                    </svg>
                    Desativar
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-pagination-footer">
        <span>Mostrando <strong>{{ professoresFiltrados.length }}</strong> de <strong>{{ professoresList.length }}</strong> professores</span>
      </div>
    </div>

    <!-- Modal Form (Cadastro/Edição) -->
    <BaseModal
      v-model="modalFormAberto"
      :title="modoEdicao ? 'Editar Professor' : 'Adicionar professor'"
      :subtitle="modoEdicao ? `Atualizando informações do professor #${professorSelecionado?.id}` : 'Informe os dados e modalidade de atuação do instrutor'"
    >
      <form @submit.prevent="salvarProfessor">
        <div class="form-grid">
          <!-- Nome -->
          <div class="form-group full-width">
            <label class="form-label">Nome <span class="required">*</span></label>
            <input
              v-model="formulario.nome"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.nome }"
              placeholder="Ex: Carlos Silva"
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
              :disabled="modoEdicao"
              placeholder="00000000000"
              maxlength="14"
              required
            />
            <span v-if="errosForm.cpf" class="form-error">{{ errosForm.cpf }}</span>
            <span v-if="modoEdicao" class="form-hint">O CPF do professor é mantido conforme regras da entidade.</span>
          </div>

          <!-- CREF -->
          <div class="form-group">
            <label class="form-label">CREF <span class="required">*</span></label>
            <input
              v-model="formulario.cref"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.cref }"
              placeholder="Ex: 123456-G/SP"
              required
            />
            <span v-if="errosForm.cref" class="form-error">{{ errosForm.cref }}</span>
          </div>

          <!-- Modalidade -->
          <div class="form-group full-width">
            <label class="form-label">Modalidade <span class="required">*</span></label>
            <input
              v-model="formulario.modalidade"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.modalidade }"
              placeholder="Ex: Musculação, Pilates, Natação, Crossfit, Boxe"
              required
            />
            <span v-if="errosForm.modalidade" class="form-error">{{ errosForm.modalidade }}</span>
            <span class="form-hint">Importante: a turma só aceitará o professor se a modalidade coincidir exatamente.</span>
          </div>

          <!-- Telefone -->
          <div class="form-group">
            <label class="form-label">Telefone</label>
            <input
              v-model="formulario.telefone"
              type="text"
              class="form-control"
              placeholder="(00) 00000-0000"
            />
          </div>

          <!-- Email -->
          <div class="form-group">
            <label class="form-label">E-mail</label>
            <input
              v-model="formulario.email"
              type="email"
              class="form-control"
              placeholder="professor@gym.com"
            />
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
            {{ modoEdicao ? 'Atualizar Professor' : 'Cadastrar Professor' }}
          </button>
        </div>
      </form>
    </BaseModal>

    <!-- Modal de Confirmação de Inativação -->
    <ConfirmModal
      v-model="modalDesativarAberto"
      title="Desativar Professor"
      :message="`Tem certeza que deseja inativar o professor '${professorSelecionado?.nome}'? Turmas ativas não poderão receber professores inativos.`"
      confirm-button-text="Sim, Desativar"
      :loading="inativando"
      @confirm="executarInativacao"
      @cancel="professorSelecionado = null"
    />
  </div>
</template>

<script setup lang="ts">
import { useGymApi } from '~/composables/useGymApi';
import { useToast } from '~/composables/useToast';
import type { Professor } from '~/types/api';
import BaseModal from '~/components/common/BaseModal.vue';
import ConfirmModal from '~/components/common/ConfirmModal.vue';
import SituaçãoBadge from '~/components/common/StatusBadge.vue';
import LoadingSpinner from '~/components/common/LoadingSpinner.vue';
import EmptyState from '~/components/common/EmptyState.vue';

const { professores } = useGymApi();
const toast = useToast();

const loading = ref(false);
const salvando = ref(false);
const inativando = ref(false);
const erroGeral = ref<string | null>(null);

const professoresList = ref<Professor[]>([]);
const filtroBusca = ref('');
const filtroSituação = ref<'TODOS' | 'ATIVO' | 'INATIVO'>('TODOS');

const modalFormAberto = ref(false);
const modalDesativarAberto = ref(false);
const modoEdicao = ref(false);
const professorSelecionado = ref<Professor | null>(null);

const formulario = reactive({
  nome: '',
  cpf: '',
  cref: '',
  modalidade: '',
  telefone: '',
  email: ''
});

const errosForm = reactive({
  nome: '',
  cpf: '',
  cref: '',
  modalidade: ''
});

const limparErros = () => {
  errosForm.nome = '';
  errosForm.cpf = '';
  errosForm.cref = '';
  errosForm.modalidade = '';
};

const formatarCpf = (cpf?: string): string => {
  if (!cpf) return '-';
  const clean = cpf.replace(/\D/g, '');
  if (clean.length === 11) {
    return clean.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }
  return cpf;
};

const professoresFiltrados = computed(() => {
  return professoresList.value.filter(prof => {
    if (filtroSituação.value !== 'TODOS' && prof.status !== filtroSituação.value) {
      return false;
    }

    if (filtroBusca.value.trim()) {
      const q = filtroBusca.value.toLowerCase();
      const matchNome = prof.nome?.toLowerCase().includes(q);
      const matchCref = prof.cref?.toLowerCase().includes(q);
      const matchMod = prof.modalidade?.toLowerCase().includes(q);
      return matchNome || matchCref || matchMod;
    }

    return true;
  });
});

const carregarProfessores = async () => {
  loading.value = true;
  erroGeral.value = null;
  try {
    const data = await professores.listar();
    professoresList.value = data || [];
  } catch (e: any) {
    erroGeral.value = e.message;
    toast.error(e.message, 'Erro ao carregar professores');
  } finally {
    loading.value = false;
  }
};

const abrirModalCadastro = () => {
  modoEdicao.value = false;
  professorSelecionado.value = null;
  limparErros();

  formulario.nome = '';
  formulario.cpf = '';
  formulario.cref = '';
  formulario.modalidade = '';
  formulario.telefone = '';
  formulario.email = '';

  modalFormAberto.value = true;
};

const abrirModalEdicao = (prof: Professor) => {
  modoEdicao.value = true;
  professorSelecionado.value = prof;
  limparErros();

  formulario.nome = prof.nome || '';
  formulario.cpf = prof.cpf || '';
  formulario.cref = prof.cref || '';
  formulario.modalidade = prof.modalidade || '';
  formulario.telefone = prof.telefone || '';
  formulario.email = prof.email || '';

  modalFormAberto.value = true;
};

const validarFormulario = (): boolean => {
  limparErros();
  let valido = true;

  if (!formulario.nome.trim()) {
    errosForm.nome = 'O nome do professor é obrigatório.';
    valido = false;
  }

  const cleanCpf = formulario.cpf.replace(/\D/g, '');
  if (!cleanCpf || cleanCpf.length !== 11) {
    errosForm.cpf = 'Informe um CPF válido com 11 dígitos.';
    valido = false;
  }

  if (!formulario.cref.trim()) {
    errosForm.cref = 'O registro do CREF é obrigatório.';
    valido = false;
  }

  if (!formulario.modalidade.trim()) {
    errosForm.modalidade = 'Informe a modalidade de atuação.';
    valido = false;
  }

  return valido;
};

const salvarProfessor = async () => {
  if (!validarFormulario()) return;

  salvando.value = true;
  const payload: Professor = {
    nome: formulario.nome.trim(),
    cpf: formulario.cpf.replace(/\D/g, ''),
    cref: formulario.cref.trim(),
    modalidade: formulario.modalidade.trim(),
    telefone: formulario.telefone.trim(),
    email: formulario.email.trim(),
    status: modoEdicao.value ? professorSelecionado.value?.status : 'ATIVO'
  };

  try {
    if (modoEdicao.value && professorSelecionado.value?.id) {
      const atualizado = await professores.atualizar(professorSelecionado.value.id, payload);
      toast.success(`Professor ${atualizado.nome} atualizado com sucesso!`);
    } else {
      const criado = await professores.cadastrar(payload);
      toast.success(`Professor ${criado.nome} cadastrado com sucesso!`);
    }

    modalFormAberto.value = false;
    await carregarProfessores();
  } catch (e: any) {
    const msg = e.message || 'Erro ao salvar professor';
    if (msg.toLowerCase().includes('cpf')) {
      errosForm.cpf = msg;
    } else if (msg.toLowerCase().includes('cref')) {
      errosForm.cref = msg;
    } else {
      toast.error(msg, 'Falha na gravação');
    }
  } finally {
    salvando.value = false;
  }
};

const solicitarInativacao = (prof: Professor) => {
  professorSelecionado.value = prof;
  modalDesativarAberto.value = true;
};

const executarInativacao = async () => {
  if (!professorSelecionado.value?.id) return;

  inativando.value = true;
  try {
    await professores.inativar(professorSelecionado.value.id);
    toast.success(`Professor '${professorSelecionado.value.nome}' inativado com sucesso.`);
    modalDesativarAberto.value = false;
    professorSelecionado.value = null;
    await carregarProfessores();
  } catch (e: any) {
    toast.error(e.message, 'Erro ao inativar professor');
  } finally {
    inativando.value = false;
  }
};

onMounted(() => {
  carregarProfessores();
});
</script>
