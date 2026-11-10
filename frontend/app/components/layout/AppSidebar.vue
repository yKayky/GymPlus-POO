<template>
  <aside class="app-sidebar" :class="{ 'mobile-open': isOpen }">
    <NuxtLink to="/dashboard" class="sidebar-brand" @click="closeMobile">
      <span class="brand-mark">G+</span>
      <span class="brand-copy"><strong>GYM<span>+</span></strong><small>Gestão da academia</small></span>
    </NuxtLink>

    <nav class="sidebar-nav" aria-label="Menu principal">
      <p class="nav-caption">MENU PRINCIPAL</p>
      <NuxtLink to="/dashboard" class="nav-link" @click="closeMobile">
        <LayoutDashboard :size="19" /><span>Início</span>
      </NuxtLink>
      <NuxtLink to="/alunos" class="nav-link" @click="closeMobile">
        <Users :size="19" /><span>Alunos</span>
      </NuxtLink>
      <NuxtLink to="/professores" class="nav-link" @click="closeMobile">
        <GraduationCap :size="19" /><span>Professores</span>
      </NuxtLink>
      <NuxtLink to="/turmas" class="nav-link" @click="closeMobile">
        <CalendarDays :size="19" /><span>Turmas</span>
      </NuxtLink>

      <p class="nav-caption nav-caption-spaced">FINANCEIRO E ACESSO</p>
      <NuxtLink to="/planos" class="nav-link" @click="closeMobile">
        <Wallet :size="19" /><span>Planos</span>
      </NuxtLink>
      <NuxtLink to="/matriculas" class="nav-link" @click="closeMobile">
        <ClipboardList :size="19" /><span>Matrículas</span>
      </NuxtLink>
      <NuxtLink v-if="isAdmin" to="/usuarios" class="nav-link" @click="closeMobile">
        <ShieldCheck :size="19" /><span>Usuários</span>
      </NuxtLink>
    </nav>

    <div class="sidebar-bottom">
      <div class="sidebar-note">
        <span class="note-icon"><Dumbbell :size="17" /></span>
        <div><strong>Sua academia, organizada.</strong><small>Controle tudo em um só lugar.</small></div>
      </div>
      <span class="sidebar-version">GYM+ · Sistema de gestão</span>
    </div>
  </aside>
  <button v-if="isOpen" class="sidebar-overlay" aria-label="Fechar menu" @click="closeMobile"></button>
</template>

<script setup lang="ts">
import {
  LayoutDashboard, Users, GraduationCap, CalendarDays,
  Wallet, ClipboardList, ShieldCheck, Dumbbell
} from 'lucide-vue-next';
import { useAuthRole } from '~/composables/useAuthRole';

const { isAdmin } = useAuthRole();
defineProps<{ isOpen?: boolean }>();
const emit = defineEmits<{ (e: 'update:isOpen', value: boolean): void }>();
const closeMobile = () => emit('update:isOpen', false);
</script>
