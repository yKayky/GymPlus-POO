<template>
  <div class="empty-state">
    <div class="empty-icon">
      <slot name="icon">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="8" y1="12" x2="16" y2="12"></line>
        </svg>
      </slot>
    </div>
    <h4>{{ title || 'Nenhum registro encontrado' }}</h4>
    <p>{{ message || 'Não há dados cadastrados ou os filtros atuais não retornaram nenhum item.' }}</p>
    <slot name="action" />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title?: string;
  message?: string;
}>();
</script>
