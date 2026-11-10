<template>
  <div class="loading-container">
    <div class="spinner"></div>
    <p v-if="text">{{ text }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  text?: string;
}>();
</script>
