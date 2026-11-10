<script setup lang="ts">
const token = useCookie<string | null>('gym_token');
await navigateTo(token.value ? '/dashboard' : '/login', { replace: true });
</script>
<template><div /></template>
