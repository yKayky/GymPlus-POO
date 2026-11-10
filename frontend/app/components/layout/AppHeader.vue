<template>
  <header class="app-header">
    <div class="header-start">
      <button class="mobile-toggle-btn" aria-label="Abrir menu" @click="$emit('toggleSidebar')">
        <Menu :size="21" />
      </button>
      <div class="header-title">
        <span class="header-eyebrow">GYM+ / ACADEMIA</span>
        <strong>{{ currentRouteName }}</strong>
      </div>
    </div>
    <div class="header-end">
      <div class="profile-chip">
        <span class="profile-avatar"><UserRound :size="17" /></span>
        <span class="profile-text"><strong>{{ currentUserName }}</strong><small>{{ isAdmin ? 'Administrador' : 'Funcionário' }}</small></span><button class="logout-button" type="button" @click="logout">Sair</button>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { Menu, UserRound } from 'lucide-vue-next';
import { useAuthRole } from '~/composables/useAuthRole';

defineEmits<{ (e: 'toggleSidebar'): void }>();
const route = useRoute();
const { currentUserName, isAdmin, logout } = useAuthRole();

const routeNames: Record<string, string> = {
  '/dashboard': 'Visão geral',
  '/alunos': 'Alunos',
  '/professores': 'Professores',
  '/turmas': 'Turmas',
  '/planos': 'Planos',
  '/matriculas': 'Matrículas',
  '/usuarios': 'Usuários'
};
const currentRouteName = computed(() => routeNames['/' + route.path.split('/')[1]] || 'Visão geral');
</script>

<style scoped>.logout-button{margin-left:14px;border:1px solid #dce5de;background:#fff;border-radius:8px;padding:8px 12px;color:#405348;font-weight:700;cursor:pointer}</style>
