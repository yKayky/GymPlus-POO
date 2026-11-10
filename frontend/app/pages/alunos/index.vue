<template>
  <div>
    <!-- Cabeçalho -->
    <div class="page-header">
      <div class="header-titles">
        <h1>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--primary);">
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
          Alunos
        </h1>
        <p>Cadastre e acompanhe os alunos da academia.</p>
      </div>

      <div class="header-actions">
        <button class="btn btn-secondary btn-sm" :disabled="loading" @click="carregarAlunos">
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
          Adicionar aluno
        </button>
      </div>
    </div>

    <!-- Feedback de Erro Geral -->
    <div v-if="erroGeral" class="banner-alert banner-danger">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="8" x2="12" y2="12"></line>
        <line x1="12" y1="16" x2="12.01" y2="16"></line>
      </svg>
      <div style="flex: 1;">
        <strong>Não foi possível carregar os dados:</strong>
        <p style="margin-top: 0.25rem;">{{ erroGeral }}</p>
      </div>
      <button class="btn btn-sm btn-outline" style="border-color: currentColor; color: inherit;" @click="carregarAlunos">
        Tentar de novo
      </button>
    </div>

    <!-- Tabela de Alunos -->
    <div class="gym-table-wrapper">
      <!-- Barra de Filtros -->
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

      <!-- Estado Carregando -->
      <LoadingSpinner v-if="loading && alunosList.length === 0" text="Carregando lista de alunos..." />

      <!-- Estado Vazio -->
      <EmptyState
        v-else-if="alunosFiltrados.length === 0"
        title="Nenhum aluno encontrado"
        message="Não foram encontrados alunos cadastrados ou correspondentes aos filtros selecionados."
      >
        <template #action>
          <button class="btn btn-primary btn-sm" @click="abrirModalCadastro">
            Cadastrar Primeiro Aluno
          </button>
        </template>
      </EmptyState>

      <!-- Dados -->
      <div v-else class="table-responsive">
        <table class="gym-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>CPF</th>
              <th>Data Nasc.</th>
              <th>Telefone</th>
              <th>Situação</th>
              <th style="text-align: right;">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="aluno in alunosFiltrados" :key="aluno.id">
              <td style="font-family: monospace; font-size: 0.85rem; color: var(--text-muted);">#{{ aluno.id }}</td>
              <td class="primary-cell">{{ aluno.nome }}</td>
              <td>{{ formatarCpf(aluno.cpf) }}</td>
              <td>{{ formatarData(aluno.dataNascimento) }}</td>
              <td>
                <div style="font-size: 0.85rem;">
                  <div>{{ aluno.email || '-' }}</div>
                  <div style="color: var(--text-muted); font-size: 0.775rem;">{{ aluno.telefone || '-' }}</div>
                </div>
              </td>
              <td>
                <SituaçãoBadge :status="aluno.status" />
              </td>
              <td class="actions-cell">
                <div class="row-actions">
                  <button
                    class="btn btn-secondary btn-sm"
                    title="Editar informações do aluno"
                    @click="abrirModalEdicao(aluno)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                    Editar
                  </button>

                  <button
                    v-if="aluno.status === 'ATIVO'"
                    class="btn btn-danger-outline btn-sm"
                    title="Desativar este aluno"
                    @click="solicitarInativacao(aluno)"
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
        <span>Mostrando <strong>{{ alunosFiltrados.length }}</strong> de <strong>{{ alunosList.length }}</strong> alunos</span>
      </div>
    </div>

    <!-- Modal de Formulário (Cadastro / Edição) -->
    <BaseModal
      v-model="modalFormAberto"
      :title="modoEdicao ? 'Editar Aluno' : 'Adicionar aluno'"
      :subtitle="modoEdicao ? `Atualizando dados do aluno #${alunoSelecionado?.id}` : 'Preencha os campos para cadastrar um novo aluno'"
    >
      <form @submit.prevent="salvarAluno">
        <div class="form-grid">
          <!-- Nome -->
          <div class="form-group full-width">
            <label class="form-label">
              Nome <span class="required">*</span>
            </label>
            <input
              v-model="formulario.nome"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.nome }"
              placeholder="Ex: João da Silva"
              required
            />
            <span v-if="errosForm.nome" class="form-error">{{ errosForm.nome }}</span>
          </div>

          <!-- CPF -->
          <div class="form-group">
            <label class="form-label">
              CPF (somente números) <span class="required">*</span>
            </label>
            <input
              v-model="formulario.cpf"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.cpf }"
              placeholder="000.000.000-00 ou 11 dígitos"
              maxlength="14"
              required
            />
            <span v-if="errosForm.cpf" class="form-error">{{ errosForm.cpf }}</span>
          </div>

          <!-- Nascimento com validação de data futura -->
          <div class="form-group">
            <label class="form-label">
              Nascimento <span class="required">*</span>
            </label>
            <input
              v-model="formulario.dataNascimento"
              type="date"
              class="form-control"
              :class="{ 'has-error': errosForm.dataNascimento }"
              :max="dataMaximaHoje"
              required
            />
            <span v-if="errosForm.dataNascimento" class="form-error">{{ errosForm.dataNascimento }}</span>
            <span class="form-hint">A data não pode ser futura conforme regra do backend.</span>
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
              :class="{ 'has-error': errosForm.email }"
              placeholder="aluno@email.com"
            />
            <span v-if="errosForm.email" class="form-error">{{ errosForm.email }}</span>
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
            {{ modoEdicao ? 'Atualizar Aluno' : 'Cadastrar Aluno' }}
          </button>
        </div>
      </form>
    </BaseModal>

    <!-- Modal de Confirmação de Inativação -->
    <ConfirmModal
      v-model="modalDesativarAberto"
      title="Desativar Aluno"
      :message="`Tem certeza que deseja inativar o cadastro de '${alunoSelecionado?.nome}'? O aluno não poderá ser incluído em novas matrículas enquanto estiver inativo.`"
      confirm-button-text="Sim, Desativar"
      :loading="inativando"
      @confirm="executarInativacao"
      @cancel="alunoSelecionado = null"
    />
  </div>
</template>

<script setup lang="ts">
import { useGymApi } from '~/composables/useGymApi';
import { useToast } from '~/composables/useToast';
import type { Aluno } from '~/types/api';
import BaseModal from '~/components/common/BaseModal.vue';
import ConfirmModal from '~/components/common/ConfirmModal.vue';
import SituaçãoBadge from '~/components/common/StatusBadge.vue';
import LoadingSpinner from '~/components/common/LoadingSpinner.vue';
import EmptyState from '~/components/common/EmptyState.vue';

const { alunos } = useGymApi();
const toast = useToast();

const loading = ref(false);
const salvando = ref(false);
const inativando = ref(false);
const erroGeral = ref<string | null>(null);

const alunosList = ref<Aluno[]>([]);
const filtroBusca = ref('');
const filtroSituação = ref<'TODOS' | 'ATIVO' | 'INATIVO'>('TODOS');

const modalFormAberto = ref(false);
const modalDesativarAberto = ref(false);
const modoEdicao = ref(false);
const alunoSelecionado = ref<Aluno | null>(null);

const formulario = reactive({
  nome: '',
  cpf: '',
  dataNascimento: '',
  telefone: '',
  email: ''
});

const errosForm = reactive({
  nome: '',
  cpf: '',
  dataNascimento: '',
  email: ''
});

// Limite máximo para a data de nascimento: data atual (ISO yyyy-MM-dd)
const dataMaximaHoje = computed(() => {
  const d = new Date();
  return d.toISOString().split('T')[0];
});

const limparErros = () => {
  errosForm.nome = '';
  errosForm.cpf = '';
  errosForm.dataNascimento = '';
  errosForm.email = '';
};

const formatarCpf = (cpf?: string): string => {
  if (!cpf) return '-';
  const clean = cpf.replace(/\D/g, '');
  if (clean.length === 11) {
    return clean.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }
  return cpf;
};

const formatarData = (dataStr?: string): string => {
  if (!dataStr) return '-';
  const parts = dataStr.split('-');
  if (parts.length === 3) {
    return `${parts[2]}/${parts[1]}/${parts[0]}`;
  }
  return dataStr;
};

const alunosFiltrados = computed(() => {
  return alunosList.value.filter(aluno => {
    // Filtro de status
    if (filtroSituação.value !== 'TODOS' && aluno.status !== filtroSituação.value) {
      return false;
    }

    // Filtro de busca
    if (filtroBusca.value.trim()) {
      const q = filtroBusca.value.toLowerCase();
      const matchNome = aluno.nome?.toLowerCase().includes(q);
      const matchCpf = aluno.cpf?.toLowerCase().includes(q);
      const matchEmail = aluno.email?.toLowerCase().includes(q);
      return matchNome || matchCpf || matchEmail;
    }

    return true;
  });
});

const carregarAlunos = async () => {
  loading.value = true;
  erroGeral.value = null;
  try {
    const data = await alunos.listar();
    alunosList.value = data || [];
  } catch (e: any) {
    erroGeral.value = e.message;
    toast.error(e.message, 'Erro ao carregar alunos');
  } finally {
    loading.value = false;
  }
};

const abrirModalCadastro = () => {
  modoEdicao.value = false;
  alunoSelecionado.value = null;
  limparErros();

  formulario.nome = '';
  formulario.cpf = '';
  formulario.dataNascimento = '';
  formulario.telefone = '';
  formulario.email = '';

  modalFormAberto.value = true;
};

const abrirModalEdicao = (aluno: Aluno) => {
  modoEdicao.value = true;
  alunoSelecionado.value = aluno;
  limparErros();

  formulario.nome = aluno.nome || '';
  formulario.cpf = aluno.cpf || '';
  formulario.dataNascimento = aluno.dataNascimento || '';
  formulario.telefone = aluno.telefone || '';
  formulario.email = aluno.email || '';

  modalFormAberto.value = true;
};

const validarFormulario = (): boolean => {
  limparErros();
  let valido = true;

  if (!formulario.nome.trim()) {
    errosForm.nome = 'O nome do aluno é obrigatório.';
    valido = false;
  }

  const cleanCpf = formulario.cpf.replace(/\D/g, '');
  if (!cleanCpf || cleanCpf.length !== 11) {
    errosForm.cpf = 'Informe um CPF válido com 11 dígitos.';
    valido = false;
  }

  if (!formulario.dataNascimento) {
    errosForm.dataNascimento = 'Informe a data de nascimento.';
    valido = false;
  } else {
    // Validação de data não futura
    const dataSelec = new Date(formulario.dataNascimento + 'T00:00:00');
    const hoje = new Date();
    hoje.setHours(23, 59, 59, 999);
    if (dataSelec > hoje) {
      errosForm.dataNascimento = 'A data de nascimento não pode ser futura.';
      valido = false;
    }
  }

  return valido;
};

const salvarAluno = async () => {
  if (!validarFormulario()) return;

  salvando.value = true;
  const payload: Aluno = {
    nome: formulario.nome.trim(),
    cpf: formulario.cpf.replace(/\D/g, ''),
    dataNascimento: formulario.dataNascimento,
    telefone: formulario.telefone.trim(),
    email: formulario.email.trim(),
    status: modoEdicao.value ? alunoSelecionado.value?.status : 'ATIVO'
  };

  try {
    if (modoEdicao.value && alunoSelecionado.value?.id) {
      const atualizado = await alunos.atualizar(alunoSelecionado.value.id, payload);
      toast.success(`Dados do aluno ${atualizado.nome} atualizados com sucesso!`);
    } else {
      const criado = await alunos.cadastrar(payload);
      toast.success(`Aluno ${criado.nome} cadastrado com sucesso!`);
    }

    modalFormAberto.value = false;
    await carregarAlunos();
  } catch (e: any) {
    const msg = e.message || 'Erro ao salvar aluno';
    if (msg.toLowerCase().includes('cpf')) {
      errosForm.cpf = msg;
    } else if (msg.toLowerCase().includes('nascimento') || msg.toLowerCase().includes('futura')) {
      errosForm.dataNascimento = msg;
    } else {
      toast.error(msg, 'Falha no cadastro');
    }
  } finally {
    salvando.value = false;
  }
};

const solicitarInativacao = (aluno: Aluno) => {
  alunoSelecionado.value = aluno;
  modalDesativarAberto.value = true;
};

const executarInativacao = async () => {
  if (!alunoSelecionado.value?.id) return;

  inativando.value = true;
  try {
    await alunos.inativar(alunoSelecionado.value.id);
    toast.success(`Aluno '${alunoSelecionado.value.nome}' inativado com sucesso.`);
    modalDesativarAberto.value = false;
    alunoSelecionado.value = null;
    await carregarAlunos();
  } catch (e: any) {
    toast.error(e.message, 'Erro ao inativar aluno');
  } finally {
    inativando.value = false;
  }
};

onMounted(() => {
  carregarAlunos();
});
</script>
