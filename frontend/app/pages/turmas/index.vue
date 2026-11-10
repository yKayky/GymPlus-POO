<template>
  <div>
    <!-- Cabeçalho -->
    <div class="page-header">
      <div class="header-titles">
        <h1>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--primary);">
            <circle cx="12" cy="12" r="10"></circle>
            <polyline points="12 6 12 12 16 14"></polyline>
          </svg>
          Turmas
        </h1>
        <p>Organize os horários, professores e vagas das turmas.</p>
      </div>

      <div class="header-actions">
        <button class="btn btn-secondary btn-sm" :disabled="loading" @click="carregarTurmasEProfessores">
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
          Adicionar turma
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
      <button class="btn btn-sm btn-outline" style="border-color: currentColor; color: inherit;" @click="carregarTurmasEProfessores">
        Tentar de novo
      </button>
    </div>

    <!-- Tabela de Turmas -->
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
            placeholder="Buscar por modalidade, horário ou professor..."
          />
        </div>

        <div class="filter-group">
          <label style="font-size: 0.85rem; font-weight: 600; color: var(--text-secondary);">Situação:</label>
          <select v-model="filtroSituação" class="form-select" style="width: auto; padding: 0.5rem 0.85rem; font-size: 0.85rem;">
            <option value="TODOS">Todas</option>
            <option value="ATIVA">Ativas</option>
            <option value="INATIVA">Apenas Inativas</option>
          </select>
        </div>
      </div>

      <!-- Carregamento -->
      <LoadingSpinner v-if="loading && turmasList.length === 0" text="Carregando turmas e matrículas..." />

      <!-- Vazio -->
      <EmptyState
        v-else-if="turmasFiltradas.length === 0"
        title="Nenhuma turma cadastrada"
        message="Não foram encontradas turmas no sistema para os critérios selecionados."
      >
        <template #action>
          <button v-if="isAdmin" class="btn btn-primary btn-sm" @click="abrirModalCadastro">
            Criar Adicionar turma
          </button>
        </template>
      </EmptyState>

      <!-- Lista de Turmas -->
      <div v-else class="table-responsive">
        <table class="gym-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Modalidade</th>
              <th>Horário</th>
              <th>Professor</th>
              <th>Ocupação Real de Alunos</th>
              <th>Situação</th>
              <th style="text-align: right;">Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="turma in turmasFiltradas" :key="turma.id">
              <td style="font-family: monospace; font-size: 0.85rem; color: var(--text-muted);">#{{ turma.id }}</td>
              <td class="primary-cell">{{ turma.modalidade }}</td>
              <td>
                <span style="display: inline-flex; align-items: center; gap: 0.35rem; font-weight: 600;">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--text-muted);">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                  </svg>
                  {{ turma.horario }}
                </span>
              </td>
              <td>
                <div v-if="turma.professor">
                  <div style="font-weight: 600; color: var(--text-primary);">{{ turma.professor.nome }}</div>
                  <div style="font-size: 0.775rem; color: var(--text-muted);">
                    CREF: {{ turma.professor.cref }} • {{ turma.professor.modalidade }}
                  </div>
                </div>
                <span v-else style="color: var(--danger); font-size: 0.85rem;">Sem professor</span>
              </td>
              <td>
                <!-- Ocupação real baseada diretamente nas matrículas retornadas pela API -->
                <div style="min-width: 140px;">
                  <div style="display: flex; justify-content: space-between; font-size: 0.8rem; margin-bottom: 3px;">
                    <span :style="{ fontWeight: '700', color: isTurmaLotada(turma) ? 'var(--danger)' : 'var(--text-primary)' }">
                      {{ getAlunosAtivosCount(turma) }} / {{ turma.maximoVagas }} alunos
                    </span>
                    <span style="color: var(--text-muted);">{{ getPorcentagemOcupacao(turma) }}%</span>
                  </div>
                  <div style="width: 100%; height: 6px; background-color: var(--border-default); border-radius: 4px; overflow: hidden;">
                    <div
                      :style="{
                        width: `${Math.min(100, getPorcentagemOcupacao(turma))}%`,
                        backgroundColor: isTurmaLotada(turma) ? 'var(--danger)' : 'var(--primary)',
                        height: '100%'
                      }"
                    ></div>
                  </div>
                </div>
              </td>
              <td>
                <SituaçãoBadge :status="turma.status" />
              </td>
              <td class="actions-cell">
                <div class="row-actions">
                  <button
                    class="btn btn-outline btn-sm"
                    title="Visualizar alunos vinculados nesta turma"
                    @click="abrirDetalhesTurma(turma)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                      <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                    Alunos ({{ turma.matriculas?.length || 0 }})
                  </button>

                  <button
                    v-if="isAdmin"
                    class="btn btn-secondary btn-sm"
                    title="Editar informações da turma"
                    @click="abrirModalEdicao(turma)"
                  >
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                    </svg>
                    Editar
                  </button>

                  <button
                    v-if="isAdmin && turma.status === 'ATIVA'"
                    class="btn btn-danger-outline btn-sm"
                    title="Desativar turma"
                    @click="solicitarInativacao(turma)"
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
        <span>Mostrando <strong>{{ turmasFiltradas.length }}</strong> de <strong>{{ turmasList.length }}</strong> turmas</span>
      </div>
    </div>

    <!-- Modal Form (Cadastro/Edição) -->
    <BaseModal
      v-model="modalFormAberto"
      :title="modoEdicao ? 'Editar Turma' : 'Adicionar turma'"
      :subtitle="modoEdicao ? `Atualizando turma #${turmaSelecionada?.id}` : 'Configure o horário, modalidade, limite de vagas e selecione o professor'"
    >
      <form @submit.prevent="salvarTurma">
        <div class="form-grid">
          <!-- Modalidade -->
          <div class="form-group full-width">
            <label class="form-label">Modalidade <span class="required">*</span></label>
            <input
              v-model="formulario.modalidade"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.modalidade }"
              placeholder="Ex: Musculação, Pilates, Natação, Crossfit"
              required
            />
            <span v-if="errosForm.modalidade" class="form-error">{{ errosForm.modalidade }}</span>
            <span class="form-hint">Dica: o professor selecionado deve possuir esta mesma modalidade cadastrada.</span>
          </div>

          <!-- Horário -->
          <div class="form-group">
            <label class="form-label">Horário <span class="required">*</span></label>
            <input
              v-model="formulario.horario"
              type="text"
              class="form-control"
              :class="{ 'has-error': errosForm.horario }"
              placeholder="Ex: 08:00, 19:30, 07:00 - 08:00"
              required
            />
            <span v-if="errosForm.horario" class="form-error">{{ errosForm.horario }}</span>
          </div>

          <!-- Máximo de Vagas -->
          <div class="form-group">
            <label class="form-label">Número de vagas <span class="required">*</span></label>
            <input
              v-model.number="formulario.maximoVagas"
              type="number"
              min="1"
              class="form-control"
              :class="{ 'has-error': errosForm.maximoVagas }"
              placeholder="Ex: 20"
              required
            />
            <span v-if="errosForm.maximoVagas" class="form-error">{{ errosForm.maximoVagas }}</span>
          </div>

          <!-- Professor -->
          <div class="form-group full-width">
            <label class="form-label">Professor <span class="required">*</span></label>
            <select
              v-model="formulario.professorId"
              class="form-select"
              :class="{ 'has-error': errosForm.professorId }"
              required
            >
              <option :value="null" disabled>Escolha um professor ativo</option>
              <option
                v-for="prof in professoresAtivos"
                :key="prof.id"
                :value="prof.id"
              >
                {{ prof.nome }} — Modalidade: {{ prof.modalidade }} (CREF: {{ prof.cref }})
              </option>
            </select>
            <span v-if="errosForm.professorId" class="form-error">{{ errosForm.professorId }}</span>
            <span v-if="professoresAtivos.length === 0" class="form-hint" style="color: var(--warning-text);">
              Nenhum professor ativo cadastrado no sistema. Cadastre um professor primeiro.
            </span>
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
            {{ modoEdicao ? 'Atualizar Turma' : 'Cadastrar Turma' }}
          </button>
        </div>
      </form>
    </BaseModal>

    <!-- Modal Detalhes de Alunos da Turma -->
    <BaseModal
      v-model="modalDetalhesAberto"
      :title="`Alunos Matriculados: ${turmaSelecionada?.modalidade} (${turmaSelecionada?.horario})`"
      :subtitle="`Capacidade: ${getAlunosAtivosCount(turmaSelecionada)}/${turmaSelecionada?.maximoVagas || 0} alunos ativos`"
      size="lg"
    >
      <div v-if="!turmaSelecionada?.matriculas || turmaSelecionada.matriculas.length === 0" style="padding: 2rem 1rem; text-align: center; color: var(--text-muted);">
        Nenhum aluno matriculado nesta turma até o momento.
      </div>
      <div v-else class="table-responsive">
        <table class="gym-table">
          <thead>
            <tr>
              <th>ID Matrícula</th>
              <th>Aluno</th>
              <th>CPF</th>
              <th>Plano</th>
              <th>Vigência</th>
              <th>Situação</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="mat in turmaSelecionada.matriculas" :key="mat.id">
              <td style="font-family: monospace;">#{{ mat.id }}</td>
              <td class="primary-cell">{{ mat.aluno?.nome || '-' }}</td>
              <td>{{ mat.aluno?.cpf || '-' }}</td>
              <td>{{ mat.plano?.nome || '-' }}</td>
              <td>{{ formatarData(mat.dataInicio) }} até {{ formatarData(mat.dataFim) }}</td>
              <td>
                <SituaçãoBadge :status="mat.status" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <template #footer>
        <button class="btn btn-secondary btn-sm" @click="modalDetalhesAberto = false">Fechar</button>
      </template>
    </BaseModal>

    <!-- Confirmação de Inativação -->
    <ConfirmModal
      v-model="modalDesativarAberto"
      title="Desativar Turma"
      :message="`Deseja inativar a turma de ${turmaSelecionada?.modalidade} (${turmaSelecionada?.horario})? Novas matrículas não poderão ser realizadas para turmas inativas.`"
      confirm-button-text="Sim, Desativar"
      :loading="inativando"
      @confirm="executarInativacao"
      @cancel="turmaSelecionada = null"
    />
  </div>
</template>

<script setup lang="ts">
import { useGymApi } from '~/composables/useGymApi';
import { useToast } from '~/composables/useToast';
import { useAuthRole } from '~/composables/useAuthRole';
import type { Turma, Professor } from '~/types/api';
import BaseModal from '~/components/common/BaseModal.vue';
import ConfirmModal from '~/components/common/ConfirmModal.vue';
import SituaçãoBadge from '~/components/common/StatusBadge.vue';
import LoadingSpinner from '~/components/common/LoadingSpinner.vue';
import EmptyState from '~/components/common/EmptyState.vue';

const { turmas, professores } = useGymApi();
const { isAdmin } = useAuthRole();
const toast = useToast();

const loading = ref(false);
const salvando = ref(false);
const inativando = ref(false);
const erroGeral = ref<string | null>(null);

const turmasList = ref<Turma[]>([]);
const professoresList = ref<Professor[]>([]);
const filtroBusca = ref('');
const filtroSituação = ref<'TODOS' | 'ATIVA' | 'INATIVA'>('TODOS');

const modalFormAberto = ref(false);
const modalDesativarAberto = ref(false);
const modalDetalhesAberto = ref(false);
const modoEdicao = ref(false);
const turmaSelecionada = ref<Turma | null>(null);

const formulario = reactive({
  modalidade: '',
  horario: '',
  maximoVagas: 20,
  professorId: null as number | null
});

const errosForm = reactive({
  modalidade: '',
  horario: '',
  maximoVagas: '',
  professorId: ''
});

const limparErros = () => {
  errosForm.modalidade = '';
  errosForm.horario = '';
  errosForm.maximoVagas = '';
  errosForm.professorId = '';
};

const professoresAtivos = computed(() => {
  return professoresList.value.filter(p => p.status === 'ATIVO');
});

const getAlunosAtivosCount = (turma?: Turma | null): number => {
  if (!turma || !turma.matriculas) return 0;
  return turma.matriculas.filter(m => m.status === 'ATIVA').length;
};

const getPorcentagemOcupacao = (turma: Turma): number => {
  const vagas = turma.maximoVagas || 1;
  const ocupadas = getAlunosAtivosCount(turma);
  return Math.round((ocupadas / vagas) * 100);
};

const isTurmaLotada = (turma: Turma): boolean => {
  return getAlunosAtivosCount(turma) >= turma.maximoVagas;
};

const formatarData = (dataStr?: string): string => {
  if (!dataStr) return '-';
  const parts = dataStr.split('-');
  if (parts.length === 3) {
    return `${parts[2]}/${parts[1]}/${parts[0]}`;
  }
  return dataStr;
};

const turmasFiltradas = computed(() => {
  return turmasList.value.filter(turma => {
    if (filtroSituação.value !== 'TODOS' && turma.status !== filtroSituação.value) {
      return false;
    }

    if (filtroBusca.value.trim()) {
      const q = filtroBusca.value.toLowerCase();
      const matchMod = turma.modalidade?.toLowerCase().includes(q);
      const matchHor = turma.horario?.toLowerCase().includes(q);
      const matchProf = turma.professor?.nome?.toLowerCase().includes(q);
      return matchMod || matchHor || matchProf;
    }

    return true;
  });
});

const carregarTurmasEProfessores = async () => {
  loading.value = true;
  erroGeral.value = null;
  try {
    const [tData, pData] = await Promise.all([
      turmas.listar(),
      professores.listar()
    ]);
    turmasList.value = tData || [];
    professoresList.value = pData || [];
  } catch (e: any) {
    erroGeral.value = e.message;
    toast.error(e.message, 'Erro ao carregar dados de turmas');
  } finally {
    loading.value = false;
  }
};

const abrirModalCadastro = () => {
  modoEdicao.value = false;
  turmaSelecionada.value = null;
  limparErros();

  formulario.modalidade = '';
  formulario.horario = '';
  formulario.maximoVagas = 20;
  formulario.professorId = professoresAtivos.value.length > 0 ? (professoresAtivos.value[0].id ?? null) : null;

  modalFormAberto.value = true;
};

const abrirModalEdicao = (turma: Turma) => {
  modoEdicao.value = true;
  turmaSelecionada.value = turma;
  limparErros();

  formulario.modalidade = turma.modalidade || '';
  formulario.horario = turma.horario || '';
  formulario.maximoVagas = turma.maximoVagas || 20;
  formulario.professorId = turma.professor?.id || null;

  modalFormAberto.value = true;
};

const abrirDetalhesTurma = (turma: Turma) => {
  turmaSelecionada.value = turma;
  modalDetalhesAberto.value = true;
};

const validarFormulario = (): boolean => {
  limparErros();
  let valido = true;

  if (!formulario.modalidade.trim()) {
    errosForm.modalidade = 'Informe a modalidade da turma.';
    valido = false;
  }

  if (!formulario.horario.trim()) {
    errosForm.horario = 'Informe o horário da turma.';
    valido = false;
  }

  if (!formulario.maximoVagas || formulario.maximoVagas <= 0) {
    errosForm.maximoVagas = 'O número máximo de vagas deve ser maior que zero.';
    valido = false;
  }

  if (!formulario.professorId) {
    errosForm.professorId = 'Selecione um professor para a turma.';
    valido = false;
  } else {
    // Validação da regra do backend: professor modalidade == turma modalidade
    const prof = professoresList.value.find(p => p.id === formulario.professorId);
    if (prof && prof.modalidade && formulario.modalidade) {
      if (prof.modalidade.trim().toLowerCase() !== formulario.modalidade.trim().toLowerCase()) {
        errosForm.professorId = `O professor selecionado leciona '${prof.modalidade}', que não corresponde à modalidade da turma ('${formulario.modalidade}').`;
        valido = false;
      }
    }
  }

  return valido;
};

const salvarTurma = async () => {
  if (!validarFormulario()) return;

  salvando.value = true;

  const profEncontrado = professoresList.value.find(p => p.id === formulario.professorId);
  const payload: Turma = {
    modalidade: formulario.modalidade.trim(),
    horario: formulario.horario.trim(),
    maximoVagas: Number(formulario.maximoVagas),
    professor: profEncontrado,
    status: modoEdicao.value ? (turmaSelecionada.value?.status || 'ATIVA') : 'ATIVA'
  };

  try {
    if (modoEdicao.value && turmaSelecionada.value?.id) {
      const atualizada = await turmas.atualizar(turmaSelecionada.value.id, payload);
      toast.success(`Turma de ${atualizada.modalidade} atualizada com sucesso!`);
    } else {
      const criada = await turmas.cadastrar(payload);
      toast.success(`Turma de ${criada.modalidade} (${criada.horario}) criada com sucesso!`);
    }

    modalFormAberto.value = false;
    await carregarTurmasEProfessores();
  } catch (e: any) {
    const msg = e.message || 'Erro ao salvar turma';
    if (msg.toLowerCase().includes('modalidade')) {
      errosForm.professorId = msg;
    } else if (msg.toLowerCase().includes('vagas')) {
      errosForm.maximoVagas = msg;
    } else {
      toast.error(msg, 'Falha ao salvar turma');
    }
  } finally {
    salvando.value = false;
  }
};

const solicitarInativacao = (turma: Turma) => {
  turmaSelecionada.value = turma;
  modalDesativarAberto.value = true;
};

const executarInativacao = async () => {
  if (!turmaSelecionada.value?.id) return;

  inativando.value = true;
  try {
    await turmas.inativar(turmaSelecionada.value.id);
    toast.success(`Turma inativada com sucesso.`);
    modalDesativarAberto.value = false;
    turmaSelecionada.value = null;
    await carregarTurmasEProfessores();
  } catch (e: any) {
    toast.error(e.message, 'Erro ao inativar turma');
  } finally {
    inativando.value = false;
  }
};

onMounted(() => {
  carregarTurmasEProfessores();
});
</script>
