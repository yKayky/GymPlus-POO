<template>
  <div>
    <!-- Cabeçalho -->
    <div class="page-header">
      <div class="header-titles">
        <h1>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--primary);">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
          Matrículas
        </h1>
        <p>Cadastre matrículas e acompanhe planos, turmas e vencimentos.</p>
      </div>

      <div class="header-actions">
        <button class="btn btn-secondary btn-sm" :disabled="loading" @click="carregarTudo">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ spinning: loading }">
            <polyline points="23 4 23 10 17 10"></polyline>
            <polyline points="1 20 1 14 7 14"></polyline>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
          </svg>
          Atualizar
        </button>

        <button class="btn btn-primary btn-sm" @click="abrirModalNovaMatricula">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          Nova matrícula
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
      <button class="btn btn-sm btn-outline" style="border-color: currentColor; color: inherit;" @click="carregarTudo">
        Tentar de novo
      </button>
    </div>

    <!-- Tabela de Matrículas -->
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
            placeholder="Buscar por aluno, plano ou modalidade..."
          />
        </div>

        <div class="filter-group">
          <label style="font-size: 0.85rem; font-weight: 600; color: var(--text-secondary);">Situação:</label>
          <select v-model="filtroSituação" class="form-select" style="width: auto; padding: 0.5rem 0.85rem; font-size: 0.85rem;">
            <option value="TODOS">Todas</option>
            <option value="ATIVA">Ativas</option>
            <option value="CANCELADA">Canceladas</option>
          </select>
        </div>
      </div>

      <!-- Carregando -->
      <LoadingSpinner v-if="loading && matriculasList.length === 0" text="Carregando matrículas..." />

      <!-- Vazio -->
      <EmptyState
        v-else-if="matriculasFiltradas.length === 0"
        title="Nenhuma matrícula encontrada"
        message="Não há registros de matrícula que correspondam aos filtros de pesquisa."
      >
        <template #action>
          <button class="btn btn-primary btn-sm" @click="abrirModalNovaMatricula">
            Realizar Primeira Matrícula
          </button>
        </template>
      </EmptyState>

      <!-- Lista de Matrículas -->
      <div v-else class="table-responsive">
        <table class="gym-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Aluno</th>
              <th>Plano Contratado</th>
              <th>Turma & Horário</th>
              <th>Início</th>
              <th>Vencimento</th>
              <th>Situação</th>
              <th style="text-align: right;">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="mat in matriculasFiltradas" :key="mat.id">
              <td style="font-family: monospace; font-size: 0.85rem; color: var(--text-muted);">#{{ mat.id }}</td>
              <td class="primary-cell">
                <div>{{ mat.aluno?.nome || '-' }}</div>
                <div style="font-size: 0.775rem; color: var(--text-muted); font-weight: normal;">
                  CPF: {{ formatarCpf(mat.aluno?.cpf) }}
                </div>
              </td>
              <td>
                <div style="font-weight: 600; color: var(--text-primary);">{{ mat.plano?.nome || '-' }}</div>
                <div style="font-size: 0.775rem; color: var(--primary-dark);">
                  {{ formatarMoeda(mat.plano?.valorMensal) }}/mês ({{ mat.plano?.duracao }} meses)
                </div>
              </td>
              <td>
                <div style="font-weight: 600;">{{ mat.turma?.modalidade || '-' }}</div>
                <div style="font-size: 0.775rem; color: var(--text-muted);">{{ mat.turma?.horario || '-' }}</div>
              </td>
              <td>{{ formatarData(mat.dataInicio) }}</td>
              <td>
                <span :style="{ fontWeight: '600', color: isVencimentoProximo(mat) ? 'var(--warning-text)' : 'inherit' }">
                  {{ formatarData(mat.dataFim) }}
                </span>
                <div v-if="isVencimentoProximo(mat)" style="font-size: 0.72rem; color: var(--warning-text); font-weight: 700;">
                  (Vence em breve)
                </div>
              </td>
              <td>
                <SituaçãoBadge :status="mat.status" />
                <div v-if="mat.motivoCancelamento" style="font-size: 0.725rem; color: var(--danger-text); margin-top: 3px; max-width: 160px; line-height: 1.2;">
                  Motivo: {{ mat.motivoCancelamento }}
                </div>
              </td>
              <td class="actions-cell">
                <div class="row-actions">
                  <!-- Renovar Matrícula -->
                  <button
                    v-if="mat.status === 'ATIVA'"
                    class="btn btn-outline btn-sm"
                    title="Renovar esta matrícula por mais um período do plano"
                    @click="solicitarRenovacao(mat)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"></path>
                    </svg>
                    Renovar
                  </button>

                  <!-- Voltar Matrícula -->
                  <button
                    v-if="mat.status === 'ATIVA'"
                    class="btn btn-danger-outline btn-sm"
                    title="Voltar esta matrícula"
                    @click="solicitarCancelamento(mat)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <circle cx="12" cy="12" r="10"></circle>
                      <line x1="15" y1="9" x2="9" y2="15"></line>
                      <line x1="9" y1="9" x2="15" y2="15"></line>
                    </svg>
                    Voltar
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="table-pagination-footer">
        <span>Mostrando <strong>{{ matriculasFiltradas.length }}</strong> de <strong>{{ matriculasList.length }}</strong> matrículas</span>
      </div>
    </div>

    <!-- Modal Nova matrícula -->
    <BaseModal
      v-model="modalNovaMatriculaAberto"
      title="Realizar Nova matrícula"
      subtitle="Vincule um aluno ativo, um plano e uma turma com vagas disponíveis"
      size="lg"
    >
      <form @submit.prevent="salvarMatricula">
        <div class="form-grid">
          <!-- Aluno -->
          <div class="form-group full-width">
            <label class="form-label">Aluno <span class="required">*</span></label>
            <select
              v-model="formMatricula.idAluno"
              class="form-select"
              :class="{ 'has-error': errosForm.idAluno }"
              required
            >
              <option :value="null" disabled>Escolha um aluno ativo</option>
              <option
                v-for="aluno in alunosDisponiveis"
                :key="aluno.id"
                :value="aluno.id"
                :disabled="alunoJaMatriculado(aluno.id)"
              >
                {{ aluno.nome }} (CPF: {{ aluno.cpf }})
                {{ alunoJaMatriculado(aluno.id) ? '— [Já possui matrícula ativa]' : '' }}
              </option>
            </select>
            <span v-if="errosForm.idAluno" class="form-error">{{ errosForm.idAluno }}</span>
            <span class="form-hint">Regra: O aluno não pode possuir outra matrícula ativa simultânea.</span>
          </div>

          <!-- Plano -->
          <div class="form-group">
            <label class="form-label">Plano <span class="required">*</span></label>
            <select
              v-model="formMatricula.idPlano"
              class="form-select"
              :class="{ 'has-error': errosForm.idPlano }"
              required
            >
              <option :value="null" disabled>Escolha um plano ativo</option>
              <option
                v-for="plano in planosAtivos"
                :key="plano.id"
                :value="plano.id"
              >
                {{ plano.nome }} — {{ formatarMoeda(plano.valorMensal) }}/mês ({{ plano.duracao }} {{ plano.duracao === 1 ? 'mês' : 'meses' }})
              </option>
            </select>
            <span v-if="errosForm.idPlano" class="form-error">{{ errosForm.idPlano }}</span>
          </div>

          <!-- Turma -->
          <div class="form-group">
            <label class="form-label">Turma <span class="required">*</span></label>
            <select
              v-model="formMatricula.idTurma"
              class="form-select"
              :class="{ 'has-error': errosForm.idTurma }"
              required
            >
              <option :value="null" disabled>Escolha uma turma ativa</option>
              <option
                v-for="turma in turmasAtivas"
                :key="turma.id"
                :value="turma.id"
                :disabled="isTurmaSemVagas(turma)"
              >
                {{ turma.modalidade }} ({{ turma.horario }}) — {{ getVagasOcupadas(turma) }}/{{ turma.maximoVagas }} vagas
                {{ isTurmaSemVagas(turma) ? '— [Lotada]' : '' }}
              </option>
            </select>
            <span v-if="errosForm.idTurma" class="form-error">{{ errosForm.idTurma }}</span>
            <span class="form-hint">Apenas turmas com vagas remanescentes são permitidas.</span>
          </div>
        </div>

        <div style="margin-top: 1.5rem; display: flex; justify-content: flex-end; gap: 0.75rem;">
          <button
            type="button"
            class="btn btn-secondary btn-sm"
            :disabled="salvando"
            @click="modalNovaMatriculaAberto = false"
          >
            Voltar
          </button>
          <button
            type="submit"
            class="btn btn-primary btn-sm"
            :disabled="salvando"
          >
            <span v-if="salvando" class="spinner" style="width: 14px; height: 14px; border-width: 2px;"></span>
            Salvar matrícula
          </button>
        </div>
      </form>
    </BaseModal>

    <!-- Confirmação de Cancelamento com Motivo Obrigatório -->
    <ConfirmModal
      v-model="modalVoltarAberto"
      title="Voltar Matrícula"
      :message="`Deseja cancelar a matrícula do aluno '${matriculaSelecionada?.aluno?.nome}'? Conforme regras da API, o motivo do cancelamento deve ser informado.`"
      confirm-button-text="Confirmar Cancelamento"
      :require-reason="true"
      reason-placeholder="Descreva o motivo (ex: Mudança de cidade, problemas de saúde, etc.)..."
      :loading="cancelando"
      @confirm="(motivo) => executarCancelamento(motivo)"
      @cancel="matriculaSelecionada = null"
    />

    <!-- Confirmação de Renovação -->
    <ConfirmModal
      v-model="modalRenovarAberto"
      title="Renovar Matrícula"
      :message="`Deseja estender a vigência da matrícula do aluno '${matriculaSelecionada?.aluno?.nome}' por mais ${matriculaSelecionada?.plano?.duracao} meses a partir da data de vencimento atual (${formatarData(matriculaSelecionada?.dataFim)})?`"
      confirm-button-text="Sim, Renovar"
      :loading="renovando"
      @confirm="executarRenovacao"
      @cancel="matriculaSelecionada = null"
    />
  </div>
</template>

<script setup lang="ts">
import { useGymApi } from '~/composables/useGymApi';
import { useToast } from '~/composables/useToast';
import type { Matricula, Aluno, Plano, Turma, MatriculaRequest } from '~/types/api';
import BaseModal from '~/components/common/BaseModal.vue';
import ConfirmModal from '~/components/common/ConfirmModal.vue';
import SituaçãoBadge from '~/components/common/StatusBadge.vue';
import LoadingSpinner from '~/components/common/LoadingSpinner.vue';
import EmptyState from '~/components/common/EmptyState.vue';

const { matriculas, alunos, planos, turmas } = useGymApi();
const toast = useToast();

const loading = ref(false);
const salvando = ref(false);
const cancelando = ref(false);
const renovando = ref(false);
const erroGeral = ref<string | null>(null);

const matriculasList = ref<Matricula[]>([]);
const alunosList = ref<Aluno[]>([]);
const planosList = ref<Plano[]>([]);
const turmasList = ref<Turma[]>([]);

const filtroBusca = ref('');
const filtroSituação = ref<'TODOS' | 'ATIVA' | 'CANCELADA'>('TODOS');

const modalNovaMatriculaAberto = ref(false);
const modalVoltarAberto = ref(false);
const modalRenovarAberto = ref(false);
const matriculaSelecionada = ref<Matricula | null>(null);

const formMatricula = reactive({
  idAluno: null as number | null,
  idPlano: null as number | null,
  idTurma: null as number | null
});

const errosForm = reactive({
  idAluno: '',
  idPlano: '',
  idTurma: ''
});

const limparErros = () => {
  errosForm.idAluno = '';
  errosForm.idPlano = '';
  errosForm.idTurma = '';
};

const alunosDisponiveis = computed(() => {
  return alunosList.value.filter(a => a.status === 'ATIVO');
});

const planosAtivos = computed(() => {
  return planosList.value.filter(p => p.status === 'ATIVO');
});

const turmasAtivas = computed(() => {
  return turmasList.value.filter(t => t.status === 'ATIVA');
});

const alunoJaMatriculado = (alunoId?: number): boolean => {
  if (!alunoId) return false;
  return matriculasList.value.some(m => m.aluno?.id === alunoId && m.status === 'ATIVA');
};

const getVagasOcupadas = (turma: Turma): number => {
  if (!turma.matriculas) return 0;
  return turma.matriculas.filter(m => m.status === 'ATIVA').length;
};

const isTurmaSemVagas = (turma: Turma): boolean => {
  return getVagasOcupadas(turma) >= turma.maximoVagas;
};

const isVencimentoProximo = (mat: Matricula): boolean => {
  if (mat.status !== 'ATIVA' || !mat.dataFim) return false;
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);

  const em7Dias = new Date();
  em7Dias.setDate(hoje.getDate() + 7);
  em7Dias.setHours(23, 59, 59, 999);

  const fim = new Date(mat.dataFim + 'T00:00:00');
  return fim >= hoje && fim <= em7Dias;
};

const formatarMoeda = (val?: number): string => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val || 0);
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

const matriculasFiltradas = computed(() => {
  return matriculasList.value.filter(mat => {
    if (filtroSituação.value !== 'TODOS' && mat.status !== filtroSituação.value) {
      return false;
    }

    if (filtroBusca.value.trim()) {
      const q = filtroBusca.value.toLowerCase();
      const matchAluno = mat.aluno?.nome?.toLowerCase().includes(q);
      const matchPlano = mat.plano?.nome?.toLowerCase().includes(q);
      const matchTurma = mat.turma?.modalidade?.toLowerCase().includes(q);
      return matchAluno || matchPlano || matchTurma;
    }

    return true;
  });
});

const carregarTudo = async () => {
  loading.value = true;
  erroGeral.value = null;

  try {
    const [mRes, aRes, pRes, tRes] = await Promise.all([
      matriculas.listar(),
      alunos.listar(),
      planos.listar(),
      turmas.listar()
    ]);

    matriculasList.value = mRes || [];
    alunosList.value = aRes || [];
    planosList.value = pRes || [];
    turmasList.value = tRes || [];
  } catch (e: any) {
    erroGeral.value = e.message;
    toast.error(e.message, 'Erro ao carregar matrículas');
  } finally {
    loading.value = false;
  }
};

const abrirModalNovaMatricula = () => {
  limparErros();
  formMatricula.idAluno = null;
  formMatricula.idPlano = null;
  formMatricula.idTurma = null;
  modalNovaMatriculaAberto.value = true;
};

const validarFormulario = (): boolean => {
  limparErros();
  let valido = true;

  if (!formMatricula.idAluno) {
    errosForm.idAluno = 'Selecione um aluno para matricular.';
    valido = false;
  } else if (alunoJaMatriculado(formMatricula.idAluno)) {
    errosForm.idAluno = 'Este aluno já possui uma matrícula ativa no sistema.';
    valido = false;
  }

  if (!formMatricula.idPlano) {
    errosForm.idPlano = 'Selecione o plano desejado.';
    valido = false;
  }

  if (!formMatricula.idTurma) {
    errosForm.idTurma = 'Selecione a turma.';
    valido = false;
  } else {
    const t = turmasList.value.find(item => item.id === formMatricula.idTurma);
    if (t && isTurmaSemVagas(t)) {
      errosForm.idTurma = 'A turma selecionada já atingiu a capacidade máxima de vagas.';
      valido = false;
    }
  }

  return valido;
};

const salvarMatricula = async () => {
  if (!validarFormulario()) return;

  salvando.value = true;
  const payload: MatriculaRequest = {
    idAluno: Number(formMatricula.idAluno),
    idPlano: Number(formMatricula.idPlano),
    idTurma: Number(formMatricula.idTurma)
  };

  try {
    const nova = await matriculas.realizar(payload);
    toast.success(`Matrícula realizada com sucesso para ${nova.aluno?.nome}!`);
    modalNovaMatriculaAberto.value = false;
    await carregarTudo();
  } catch (e: any) {
    toast.error(e.message, 'Falha ao efetivar matrícula');
  } finally {
    salvando.value = false;
  }
};

const solicitarRenovacao = (mat: Matricula) => {
  matriculaSelecionada.value = mat;
  modalRenovarAberto.value = true;
};

const executarRenovacao = async () => {
  if (!matriculaSelecionada.value?.id) return;

  renovando.value = true;
  try {
    const renovada = await matriculas.renovar(matriculaSelecionada.value.id);
    toast.success(`Matrícula renovada até ${formatarData(renovada.dataFim)} com sucesso!`);
    modalRenovarAberto.value = false;
    matriculaSelecionada.value = null;
    await carregarTudo();
  } catch (e: any) {
    toast.error(e.message, 'Erro ao renovar matrícula');
  } finally {
    renovando.value = false;
  }
};

const solicitarCancelamento = (mat: Matricula) => {
  matriculaSelecionada.value = mat;
  modalVoltarAberto.value = true;
};

const executarCancelamento = async (motivo?: string) => {
  const matriculaId = matriculaSelecionada.value?.id;
  if (matriculaId == null) {
    toast.error('Não foi possível identificar a matrícula selecionada. Feche a janela e tente novamente.', 'Matrícula não identificada');
    return;
  }
  if (!motivo?.trim()) {
    toast.error('Informe o motivo do cancelamento antes de confirmar.', 'Motivo obrigatório');
    return;
  }

  cancelando.value = true;
  try {
    await matriculas.cancelar(matriculaId, motivo.trim());
    toast.success(`Matrícula cancelada com sucesso.`);
    modalVoltarAberto.value = false;
    matriculaSelecionada.value = null;
    await carregarTudo();
  } catch (e: any) {
    toast.error(e.message, 'Erro ao cancelar matrícula');
  } finally {
    cancelando.value = false;
  }
};

onMounted(() => {
  carregarTudo();
});
</script>
