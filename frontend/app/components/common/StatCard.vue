<template>
  <div class="stat-card">
    <div class="stat-info">
      <span class="stat-label">{{ title }}</span>
      <span class="stat-value">{{ value }}</span>
      <span v-if="subtext" class="stat-subtext">
        <slot name="trend-icon" />
        {{ subtext }}
      </span>
    </div>
    <div class="stat-icon-wrapper" :class="variant || 'primary'">
      <slot name="icon">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
        </svg>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string;
  value: string | number;
  subtext?: string;
  variant?: 'primary' | 'accent' | 'warning' | 'info';
}>();
</script>
