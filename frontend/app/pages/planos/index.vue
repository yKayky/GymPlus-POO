<template>
  <div>
    <!-- Cabeçalho -->
    <div class="page-header">
      <div class="header-titles">
        <h1>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--primary);">
            <rect width="20" height="14" x="2" y="5" rx="2"></rect>
            <line x1="2" x2="22" y1="10" y2="10"></line>
          </svg>
          Planos
        </h1>
        <p>Crie planos e defina o valor e a duração.</p>
      </div>

      <div class="header-actions">
        <button class="btn btn-secondary btn-sm" :disabled="loading" @click="carregarPlanos">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ spinning: loading }">
            <polyline points="23 4 23 10 17 10"></polyline>
            <polyline points="1 20 1 14 7 14"></polyline>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
          </svg>
          Atualizar
        </button>

        <button v-if="isAdmin" class="btn btn-primary btn-sm" @click="abrirModalCadastro">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          Adicionar plano
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
        <strong>Não foi possível carregar os dados:</strong>
        <p style="margin-top: 0.25rem;">{{ erroGeral }}</p>
      </div>
      <button class="btn btn-sm btn-outline" style="border-color: currentColor; color: inherit;" @click="carregarPlanos">
        Tentar de novo
      </button>
    </div>

    <!-- Tabela de Planos -->
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
            placeholder="Buscar por nome do plano..."
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
      <LoadingSpinner v-if="loading && planosList.length === 0" text="Carregando planos..." />

      <!-- Vazio -->
      <EmptyState
        v-else-if="planosFiltrados.length === 0"
        title="Nenhum plano cadastrado"
        message="Não foram encontrados planos cadastrados com os critérios selecionados."
      >
        <template #action>
          <button v-if="isAdmin" class="btn btn-primary btn-sm" @click="abrirModalCadastro">
            Criar Primeiro Plano
          </button>
        </template>
      </EmptyState>

      <!-- Lista de Planos -->
      <div v-else class="table-responsive">
        <table class="gym-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome do plano</th>
              <th>Duração</th>
              <th>Valor Mensal</th>
              <th>Situação</th>
              <th style="text-align: right;">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="plano in planosFiltrados" :key="plano.id">
              <td style="font-family: monospace; font-size: 0.85rem; color: var(--text-muted);">#{{ plano.id }}</td>
              <td class="primary-cell">{{ plano.nome }}</td>
              <td>
                <span style="font-weight: 600; color: var(--text-primary);">
                  {{ plano.duracao }} {{ plano.duracao === 1 ? 'mês' : 'meses' }}
                </span>
              </td>
              <td>
                <span style="font-weight: 700; color: var(--primary-dark); font-size: 1rem;">
                  {{ formatarMoeda(plano.valorMensal) }}
                </span>
                <span style="font-size: 0.775rem; color: var(--text-muted);"> /mês</span>
              </td>
              <td>
                <SituaçãoBadge :status="plano.status" />
              </td>
              <td class="actions-cell">
                <div class="row-actions">
                  <button
                    v-if="isAdmin"
                    class="btn btn-secondary btn-sm"
                    title="Editar informações do plano"
                    @click="abrirModalEdicao(plano)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                    Editar
                  </button>

                  <button
                    v-if="isAdmin && plano.status === 'ATIVO'"
                    class="btn btn-danger-outline btn-sm"
                    title="Desativar este plano"
                    @click="solicitarInativacao(plano)"
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
        <span>Mostrando <strong>{{ planosFiltrados.length }}</strong> de <strong>{{ planosList.length }}</strong> planos</span>
      </div>
    </div>

    <!-- Modal Form (Cadastro/Edição) -->
    <BaseModal
      v-model="modalFormAberto"
      :title="modoEdicao ? 'Editar Plano' : 'Adicionar plano'"
      :subtitle="modoEdicao ? `Atualizando plano #${planoSelecionado?.id}` : 'Informe os parâmetros do plano conforme validações da API'"
    >
      <form @submit.prevent="salvarPlano">
        <div class="form-grid">
          <!-- Nome -->
          <div class="form-group full-width">
            <label class="form-label">Nome do plano <span class="required">*</span></label>
            <input
              v-model="formulario.nome"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.nome }"
              placeholder="Ex: Mensal Básico, Semestral Vip, Anual Gold"
              required
            />
            <span v-if="errosForm.nome" class="form-error">{{ errosForm.nome }}</span>
          </div>

          <!-- Duração (em meses) -->
          <div class="form-group">
            <label class="form-label">Duração (meses) <span class="required">*</span></label>
            <input
              v-model.number="formulario.duracao"
              type="number"
              min="1"
              step="1"
              class="form-control"
              :class="{ 'has-error': errosForm.duracao }"
              placeholder="Ex: 1, 3, 6, 12"
              required
            />
            <span v-if="errosForm.duracao" class="form-error">{{ errosForm.duracao }}</span>
            <span class="form-hint">O backend exige duração estritamente maior que 0.</span>
          </div>

          <!-- Valor Mensal -->
          <div class="form-group">
            <label class="form-label">Valor por mês (R$) <span class="required">*</span></label>
            <input
              v-model.number="formulario.valorMensal"
              type="number"
              min="0.01"
              step="0.01"
              class="form-control"
              :class="{ 'has-error': errosForm.valorMensal }"
              placeholder="Ex: 120.00"
              required
            />
            <span v-if="errosForm.valorMensal" class="form-error">{{ errosForm.valorMensal }}</span>
            <span class="form-hint">O backend exige valor mensal estritamente maior que 0.</span>
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
            {{ modoEdicao ? 'Atualizar Plano' : 'Cadastrar Plano' }}
          </button>
        </div>
      </form>
    </BaseModal>

    <!-- Confirmação de Inativação -->
    <ConfirmModal
      v-model="modalDesativarAberto"
      title="Desativar Plano"
      :message="`Tem certeza que deseja inativar o plano '${planoSelecionado?.nome}'? Novas matrículas não poderão ser efetuadas com planos inativos.`"
      confirm-button-text="Sim, Desativar"
      :loading="inativando"
      @confirm="executarInativacao"
      @cancel="planoSelecionado = null"
    />
  </div>
</template>

<script setup lang="ts">
import { useGymApi } from '~/composables/useGymApi';
import { useToast } from '~/composables/useToast';
import { useAuthRole } from '~/composables/useAuthRole';
import type { Plano } from '~/types/api';
import BaseModal from '~/components/common/BaseModal.vue';
import ConfirmModal from '~/components/common/ConfirmModal.vue';
import SituaçãoBadge from '~/components/common/StatusBadge.vue';
import LoadingSpinner from '~/components/common/LoadingSpinner.vue';
import EmptyState from '~/components/common/EmptyState.vue';

const { planos } = useGymApi();
const { isAdmin } = useAuthRole();
const toast = useToast();

const loading = ref(false);
const salvando = ref(false);
const inativando = ref(false);
const erroGeral = ref<string | null>(null);

const planosList = ref<Plano[]>([]);
const filtroBusca = ref('');
const filtroSituação = ref<'TODOS' | 'ATIVO' | 'INATIVO'>('TODOS');

const modalFormAberto = ref(false);
const modalDesativarAberto = ref(false);
const modoEdicao = ref(false);
const planoSelecionado = ref<Plano | null>(null);

const formulario = reactive({
  nome: '',
  duracao: 1,
  valorMensal: 100.0
});

const errosForm = reactive({
  nome: '',
  duracao: '',
  valorMensal: ''
});

const limparErros = () => {
  errosForm.nome = '';
  errosForm.duracao = '';
  errosForm.valorMensal = '';
};

const formatarMoeda = (val?: number): string => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val || 0);
};

const planosFiltrados = computed(() => {
  return planosList.value.filter(plano => {
    if (filtroSituação.value !== 'TODOS' && plano.status !== filtroSituação.value) {
      return false;
    }

    if (filtroBusca.value.trim()) {
      const q = filtroBusca.value.toLowerCase();
      return plano.nome?.toLowerCase().includes(q);
    }

    return true;
  });
});

const carregarPlanos = async () => {
  loading.value = true;
  erroGeral.value = null;
  try {
    const data = await planos.listar();
    planosList.value = data || [];
  } catch (e: any) {
    erroGeral.value = e.message;
    toast.error(e.message, 'Erro ao carregar planos');
  } finally {
    loading.value = false;
  }
};

const abrirModalCadastro = () => {
  modoEdicao.value = false;
  planoSelecionado.value = null;
  limparErros();

  formulario.nome = '';
  formulario.duracao = 1;
  formulario.valorMensal = 100.0;

  modalFormAberto.value = true;
};

const abrirModalEdicao = (plano: Plano) => {
  modoEdicao.value = true;
  planoSelecionado.value = plano;
  limparErros();

  formulario.nome = plano.nome || '';
  formulario.duracao = plano.duracao || 1;
  formulario.valorMensal = plano.valorMensal || 0;

  modalFormAberto.value = true;
};

const validarFormulario = (): boolean => {
  limparErros();
  let valido = true;

  if (!formulario.nome.trim()) {
    errosForm.nome = 'O nome do plano é obrigatório.';
    valido = false;
  }

  if (!formulario.duracao || formulario.duracao <= 0) {
    errosForm.duracao = 'A duração em meses deve ser maior que 0.';
    valido = false;
  }

  if (!formulario.valorMensal || formulario.valorMensal <= 0) {
    errosForm.valorMensal = 'O valor mensal deve ser maior que R$ 0,00.';
    valido = false;
  }

  return valido;
};

const salvarPlano = async () => {
  if (!validarFormulario()) return;

  salvando.value = true;

  // Backend exige status ATIVO no método validarPlano()
  const payload: Plano = {
    nome: formulario.nome.trim(),
    duracao: Number(formulario.duracao),
    valorMensal: Number(formulario.valorMensal),
    status: modoEdicao.value ? (planoSelecionado.value?.status || 'ATIVO') : 'ATIVO'
  };

  try {
    if (modoEdicao.value && planoSelecionado.value?.id) {
      const atualizado = await planos.atualizar(planoSelecionado.value.id, payload);
      toast.success(`Plano ${atualizado.nome} atualizado com sucesso!`);
    } else {
      const criado = await planos.cadastrar(payload);
      toast.success(`Plano ${criado.nome} cadastrado com sucesso!`);
    }

    modalFormAberto.value = false;
    await carregarPlanos();
  } catch (e: any) {
    toast.error(e.message, 'Erro ao salvar plano');
  } finally {
    salvando.value = false;
  }
};

const solicitarInativacao = (plano: Plano) => {
  planoSelecionado.value = plano;
  modalDesativarAberto.value = true;
};

const executarInativacao = async () => {
  if (!planoSelecionado.value?.id) return;

  inativando.value = true;
  try {
    await planos.inativar(planoSelecionado.value.id);
    toast.success(`Plano inativado com sucesso.`);
    modalDesativarAberto.value = false;
    planoSelecionado.value = null;
    await carregarPlanos();
  } catch (e: any) {
    toast.error(e.message, 'Erro ao inativar plano');
  } finally {
    inativando.value = false;
  }
};

onMounted(() => {
  carregarPlanos();
});
</script>
