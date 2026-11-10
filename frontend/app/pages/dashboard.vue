<template>
  <div>
    <!-- Cabeçalho da Página -->
    <div class="page-header">
      <div class="header-titles">
        <h1>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--primary);">
            <rect width="7" height="9" x="3" y="3" rx="1"></rect>
            <rect width="7" height="5" x="14" y="3" rx="1"></rect>
            <rect width="7" height="9" x="14" y="12" rx="1"></rect>
            <rect width="7" height="5" x="3" y="16" rx="1"></rect>
          </svg>
          Visão geral
        </h1>
        <p>Veja os principais números da academia em um só lugar.</p>
      </div>

      <div class="header-actions">
        <button class="btn btn-secondary btn-sm" :disabled="loading" @click="carregarDados">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ spinning: loading }">
            <polyline points="23 4 23 10 17 10"></polyline>
            <polyline points="1 20 1 14 7 14"></polyline>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
          </svg>
          Atualizar
        </button>
      </div>
    </div>

    <!-- Alerta de falha de conexão caso ocorra -->
    <div v-if="erroGeral" class="banner-alert banner-danger">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="8" x2="12" y2="12"></line>
        <line x1="12" y1="16" x2="12.01" y2="16"></line>
      </svg>
      <div style="flex: 1;">
        <strong>Erro ao carregar dados do painel:</strong>
        <p style="margin-top: 0.25rem;">{{ erroGeral }}</p>
      </div>
      <button class="btn btn-sm btn-outline" style="border-color: currentColor; color: inherit;" @click="carregarDados">
        Tentar de novo
      </button>
    </div>

    <!-- Estado de Carregamento -->
    <LoadingSpinner v-if="loading && !dadosCarregados" text="Carregando estatísticas e registros do sistema..." />

    <!-- Conteúdo do Dashboard -->
    <div v-else>
      <!-- Grade de Indicadores Reais -->
      <div class="stats-grid">
        <!-- Alunos Ativos -->
        <StatCard
          title="Alunos Ativos"
          :value="metricas.alunosAtivos"
          :subtext="`${metricas.totalAlunos} alunos no cadastro geral`"
          variant="primary"
        >
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
          </template>
        </StatCard>

        <!-- Matrículas Ativas -->
        <StatCard
          title="Matrículas Ativas"
          :value="metricas.matriculasAtivas"
          :subtext="`${metricas.matriculasCanceladas} canceladas`"
          variant="accent"
        >
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
            </svg>
          </template>
        </StatCard>

        <!-- Turmas Ativas & Ocupação -->
        <StatCard
          title="Turmas Ativas"
          :value="metricas.turmasAtivas"
          :subtext="`${metricas.vagasOcupadas} de ${metricas.vagasTotais} vagas ocupadas (${metricas.taxaOcupacao}%)`"
          variant="info"
        >
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"></circle>
              <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
          </template>
        </StatCard>

        <!-- Receita Mensal Estimada -->
        <StatCard
          title="Receita Mensal Ativa"
          :value="formatarMoeda(metricas.receitaMensalEstimada)"
          :subtext="`Baseada em ${metricas.matriculasAtivas} planos ativos`"
          variant="warning"
        >
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="1" x2="12" y2="23"></line>
              <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
            </svg>
          </template>
        </StatCard>
      </div>

      <!-- Alertas Operacionais (Matrículas Próximas ao Vencimento) -->
      <div v-if="matriculasVencendo.length > 0" class="gym-card" style="margin-bottom: 2rem; border-left: 4px solid var(--warning);">
        <div class="gym-card-header" style="background-color: var(--warning-bg); border-bottom: none;">
          <h3 style="color: var(--warning-text); font-size: 1.05rem;">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"></path>
              <line x1="12" y1="9" x2="12" y2="13"></line>
              <line x1="12" y1="17" x2="12.01" y2="17"></line>
            </svg>
            Atenção: {{ matriculasVencendo.length }} Matrícula(s) Próxima(s) do Vencimento (Próximos 7 dias)
          </h3>
          <NuxtLink to="/matriculas" class="btn btn-sm btn-outline" style="border-color: var(--warning-text); color: var(--warning-text);">
            Gerenciar Matrículas
          </NuxtLink>
        </div>
        <div class="gym-table-wrapper" style="border: none; border-radius: 0; box-shadow: none;">
          <div class="table-responsive">
            <table class="gym-table">
              <thead>
                <tr>
                  <th>Aluno</th>
                  <th>Plano</th>
                  <th>Turma</th>
                  <th>Data Término</th>
                  <th>Situação</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="mat in matriculasVencendo" :key="mat.id">
                  <td class="primary-cell">{{ mat.aluno?.nome || '-' }}</td>
                  <td>{{ mat.plano?.nome || '-' }}</td>
                  <td>{{ mat.turma?.modalidade || '-' }} ({{ mat.turma?.horario || '-' }})</td>
                  <td style="color: var(--danger); font-weight: 600;">{{ formatarData(mat.dataFim) }}</td>
                  <td>
                    <SituaçãoBadge :status="mat.status" />
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Resumo de Turmas e Professores -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(440px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">
        <!-- Card: Distribuição de Vagas por Turma -->
        <div class="gym-card">
          <div class="gym-card-header">
            <h3>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--accent);">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
              Turmas e Capacidade Real
            </h3>
            <NuxtLink to="/turmas" class="btn btn-sm btn-secondary">Ver Todas</NuxtLink>
          </div>
          <div class="gym-card-body" style="padding: 1rem 1.5rem;">
            <div v-if="turmasList.length === 0" style="padding: 2rem 0; text-align: center; color: var(--text-muted);">
              Nenhuma turma cadastrada no momento.
            </div>
            <div v-else style="display: flex; flex-direction: column; gap: 1rem;">
              <div
                v-for="turma in turmasList.slice(0, 5)"
                :key="turma.id"
                style="display: flex; flex-direction: column; gap: 0.35rem; padding-bottom: 0.75rem; border-bottom: 1px solid var(--border-subtle);"
              >
                <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.9rem;">
                  <div>
                    <span style="font-weight: 700; color: var(--text-primary);">{{ turma.modalidade }}</span>
                    <span style="color: var(--text-muted); margin-left: 0.5rem; font-size: 0.825rem;">({{ turma.horario }})</span>
                  </div>
                  <div style="font-size: 0.825rem; font-weight: 600;">
                    <span :style="{ color: isTurmaLotada(turma) ? 'var(--danger)' : 'var(--text-secondary)' }">
                      {{ getAlunosTurmaCount(turma) }} / {{ turma.maximoVagas }} vagas
                    </span>
                    <SituaçãoBadge :status="turma.status" style="margin-left: 0.5rem;" />
                  </div>
                </div>

                <!-- Barra de progresso de ocupação -->
                <div style="width: 100%; height: 6px; background-color: var(--border-default); border-radius: 4px; overflow: hidden;">
                  <div
                    :style="{
                      width: `${Math.min(100, Math.round((getAlunosTurmaCount(turma) / (turma.maximoVagas || 1)) * 100))}%`,
                      backgroundColor: isTurmaLotada(turma) ? 'var(--danger)' : 'var(--primary)',
                      height: '100%',
                      borderRadius: '4px',
                      transition: 'width 0.3s ease'
                    }"
                  ></div>
                </div>

                <div style="font-size: 0.775rem; color: var(--text-muted);">
                  Professor: <strong style="color: var(--text-secondary);">{{ turma.professor?.nome || 'Não vinculado' }}</strong>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Card: Professores e Modalidades -->
        <div class="gym-card">
          <div class="gym-card-header">
            <h3>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: var(--primary);">
                <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
              </svg>
              Professores
            </h3>
            <NuxtLink to="/professores" class="btn btn-sm btn-secondary">Ver Todos</NuxtLink>
          </div>
          <div class="gym-card-body" style="padding: 1rem 1.5rem;">
            <div v-if="professoresList.length === 0" style="padding: 2rem 0; text-align: center; color: var(--text-muted);">
              Nenhum professor cadastrado.
            </div>
            <div v-else style="display: flex; flex-direction: column; gap: 0.75rem;">
              <div
                v-for="prof in professoresList.slice(0, 5)"
                :key="prof.id"
                style="display: flex; align-items: center; justify-content: space-between; padding-bottom: 0.65rem; border-bottom: 1px solid var(--border-subtle);"
              >
                <div>
                  <div style="font-weight: 600; color: var(--text-primary); font-size: 0.925rem;">{{ prof.nome }}</div>
                  <div style="font-size: 0.8rem; color: var(--text-muted);">
                    CREF: {{ prof.cref }} • Modalidade: <strong>{{ prof.modalidade }}</strong>
                  </div>
                </div>
                <div>
                  <SituaçãoBadge :status="prof.status" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useGymApi } from '~/composables/useGymApi';
import type { Aluno, Professor, Turma, Plano, Matricula } from '~/types/api';
import StatCard from '~/components/common/StatCard.vue';
import SituaçãoBadge from '~/components/common/StatusBadge.vue';
import LoadingSpinner from '~/components/common/LoadingSpinner.vue';

const { alunos, professores, turmas, planos, matriculas } = useGymApi();

const loading = ref(false);
const dadosCarregados = ref(false);
const erroGeral = ref<string | null>(null);

const alunosList = ref<Aluno[]>([]);
const professoresList = ref<Professor[]>([]);
const turmasList = ref<Turma[]>([]);
const planosList = ref<Plano[]>([]);
const matriculasList = ref<Matricula[]>([]);

const metricas = reactive({
  totalAlunos: 0,
  alunosAtivos: 0,
  totalProfessores: 0,
  professoresAtivos: 0,
  totalTurmas: 0,
  turmasAtivas: 0,
  vagasTotais: 0,
  vagasOcupadas: 0,
  taxaOcupacao: 0,
  totalMatriculas: 0,
  matriculasAtivas: 0,
  matriculasCanceladas: 0,
  receitaMensalEstimada: 0
});

const getAlunosTurmaCount = (turma: Turma): number => {
  // Apenas matrículas ativas contam para a ocupação real
  if (!turma.matriculas) return 0;
  return turma.matriculas.filter(m => m.status === 'ATIVA').length;
};

const isTurmaLotada = (turma: Turma): boolean => {
  return getAlunosTurmaCount(turma) >= turma.maximoVagas;
};

// Identifica matrículas ativas com término nos próximos 7 dias
const matriculasVencendo = computed(() => {
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);

  const em7Dias = new Date();
  em7Dias.setDate(hoje.getDate() + 7);
  em7Dias.setHours(23, 59, 59, 999);

  return matriculasList.value.filter(m => {
    if (m.status !== 'ATIVA' || !m.dataFim) return false;
    const fim = new Date(m.dataFim + 'T00:00:00');
    return fim >= hoje && fim <= em7Dias;
  });
});

const formatarMoeda = (val: number): string => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(val || 0);
};

const formatarData = (dataStr?: string): string => {
  if (!dataStr) return '-';
  const parts = dataStr.split('-');
  if (parts.length === 3) {
    return `${parts[2]}/${parts[1]}/${parts[0]}`;
  }
  return dataStr;
};

const carregarDados = async () => {
  loading.value = true;
  erroGeral.value = null;

  try {
    const [
      resAlunos,
      resProfs,
      resTurmas,
      resPlanos,
      resMats
    ] = await Promise.allSettled([
      alunos.listar(),
      professores.listar(),
      turmas.listar(),
      planos.listar(),
      matriculas.listar()
    ]);

    if (resAlunos.status === 'fulfilled') {
      alunosList.value = resAlunos.value || [];
      metricas.totalAlunos = alunosList.value.length;
      metricas.alunosAtivos = alunosList.value.filter(a => a.status === 'ATIVO').length;
    }

    if (resProfs.status === 'fulfilled') {
      professoresList.value = resProfs.value || [];
      metricas.totalProfessores = professoresList.value.length;
      metricas.professoresAtivos = professoresList.value.filter(p => p.status === 'ATIVO').length;
    }

    if (resTurmas.status === 'fulfilled') {
      turmasList.value = resTurmas.value || [];
      metricas.totalTurmas = turmasList.value.length;
      metricas.turmasAtivas = turmasList.value.filter(t => t.status === 'ATIVA').length;

      let vagas = 0;
      let ocupadas = 0;
      turmasList.value.forEach(t => {
        if (t.status === 'ATIVA') {
          vagas += t.maximoVagas || 0;
          ocupadas += getAlunosTurmaCount(t);
        }
      });
      metricas.vagasTotais = vagas;
      metricas.vagasOcupadas = ocupadas;
      metricas.taxaOcupacao = vagas > 0 ? Math.round((ocupadas / vagas) * 100) : 0;
    }

    if (resPlanos.status === 'fulfilled') {
      planosList.value = resPlanos.value || [];
    }

    if (resMats.status === 'fulfilled') {
      matriculasList.value = resMats.value || [];
      metricas.totalMatriculas = matriculasList.value.length;
      const ativas = matriculasList.value.filter(m => m.status === 'ATIVA');
      metricas.matriculasAtivas = ativas.length;
      metricas.matriculasCanceladas = matriculasList.value.filter(m => m.status === 'CANCELADA').length;

      // Cálculo de receita mensal estimada a partir das matrículas ativas reais
      metricas.receitaMensalEstimada = ativas.reduce((acc, curr) => {
        return acc + (curr.plano?.valorMensal || 0);
      }, 0);
    }

    // Se todas falharam por conexão:
    const todasFalharam = [resAlunos, resProfs, resTurmas, resPlanos, resMats].every(r => r.status === 'rejected');
    if (todasFalharam) {
      const primeiroErro = (resAlunos as PromiseRejectedResult).reason;
      erroGeral.value = primeiroErro?.message || 'Falha ao conectar com o backend Spring Boot.';
    }

    dadosCarregados.value = true;
  } catch (e: any) {
    erroGeral.value = e?.message || 'Erro inesperado ao consultar o backend.';
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  carregarDados();
});
</script>

<style scoped>
.spinning {
  animation: spin 0.8s linear infinite;
}
</style>
