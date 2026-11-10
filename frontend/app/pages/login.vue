<script setup lang="ts">
definePageMeta({ layout: false });

const { login } = useAuthRole();
const email = ref('');
const senha = ref('');
const carregando = ref(false);
const erro = ref('');
const entrar = async () => {
  erro.value = ''; carregando.value = true;
  try { await login(email.value.trim(), senha.value); await navigateTo('/dashboard'); }
  catch (e: any) { erro.value = e?.data?.message || e?.data?.error || 'E-mail ou senha inválidos. Confira os dados e tente novamente.'; }
  finally { carregando.value = false; }
};
</script>
<template>
  <main class="login-page"><section class="login-card">
    <div class="brand-mark">G+</div><p class="eyebrow">GESTÃO DA ACADEMIA</p>
    <h1>Bem-vindo ao GYM+</h1><p class="subtitle">Entre com sua conta administrativa para continuar.</p>
    <form @submit.prevent="entrar"><label for="email">E-mail</label><input id="email" v-model="email" type="email" autocomplete="username" placeholder="seu@email.com" required />
      <label for="senha">Senha</label><input id="senha" v-model="senha" type="password" autocomplete="current-password" placeholder="Digite sua senha" required />
      <p v-if="erro" class="login-error" role="alert">{{ erro }}</p><button type="submit" :disabled="carregando">{{ carregando ? 'Entrando...' : 'Entrar no sistema' }}</button>
    </form><small>Acesso restrito a administradores e funcionários.</small>
  </section></main>
</template>
<style scoped>
.login-page{min-height:100vh;display:grid;place-items:center;padding:24px;background:#f3f6f3;font-family:Inter,sans-serif;color:#14251c}.login-card{width:min(100%,430px);padding:42px;background:#fff;border:1px solid #e1e9e2;border-radius:22px;box-shadow:0 18px 55px #193b2412}.brand-mark{width:52px;height:52px;display:grid;place-items:center;border-radius:15px;background:#a5ed27;color:#17271d;font-weight:900;font-size:22px;margin-bottom:26px}.eyebrow{font-size:11px;letter-spacing:1.5px;font-weight:800;color:#789080}.login-card h1{font-size:27px;margin:8px 0}.subtitle{color:#718078;font-size:14px;margin:0 0 28px}.login-card form{display:grid;gap:10px}.login-card label{font-size:13px;font-weight:700;margin-top:8px}.login-card input{height:48px;border:1px solid #dce5de;border-radius:10px;padding:0 14px;font:inherit;outline-color:#91d921}.login-card button{height:49px;margin-top:14px;border:0;border-radius:10px;background:#a5ed27;color:#17271d;font-weight:800;font-size:14px;cursor:pointer}.login-card button:disabled{opacity:.6;cursor:wait}.login-card small{display:block;text-align:center;color:#87938b;margin-top:24px;font-size:11px}.login-error{margin:6px 0 0;padding:11px;border-radius:8px;background:#fff0ef;color:#b42318;font-size:13px}
</style>
