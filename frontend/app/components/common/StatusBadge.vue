<template>
  <span class="badge" :class="badgeClass">
    <span class="badge-dot"></span>
    <span>{{ statusText }}</span>
  </span>
</template>

<script setup lang="ts">
const props = defineProps<{
  status?: string | null;
}>();

const badgeClass = computed(() => {
  const s = props.status?.toUpperCase();
  if (s === 'ATIVO' || s === 'ATIVA') {
    return 'badge-success';
  }
  if (s === 'INATIVO' || s === 'INATIVA' || s === 'CANCELADA') {
    return 'badge-danger';
  }
  if (s === 'PENDENTE') {
    return 'badge-warning';
  }
  return 'badge-neutral';
});

const statusText = computed(() => {
  return props.status || 'DESCONHECIDO';
});
</script>
