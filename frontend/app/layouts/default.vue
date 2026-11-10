<template>
  <div class="admin-shell">
    <!-- Barra Lateral -->
    <AppSidebar :is-open="sidebarOpen" @update:is-open="sidebarOpen = $event" />

    <!-- Área de Conteúdo Principal -->
    <div class="main-content-area">
      <!-- Cabeçalho -->
      <AppHeader @toggle-sidebar="sidebarOpen = !sidebarOpen" />

      <!-- Conteúdo da Página -->
      <main class="page-container">
        <slot />
      </main>
    </div>

    <!-- Notificações Toast Globais -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import AppSidebar from '~/components/layout/AppSidebar.vue';
import AppHeader from '~/components/layout/AppHeader.vue';
import ToastContainer from '~/components/common/ToastContainer.vue';

const sidebarOpen = ref(false);
</script>
