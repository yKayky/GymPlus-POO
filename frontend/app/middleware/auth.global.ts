export default defineNuxtRouteMiddleware((to) => {
  const token = useCookie<string | null>('gym_token');
  const user = useCookie<{ tipo: string } | null>('gym_user');
  const authenticated = Boolean(token.value && user.value?.tipo);
  if (to.path === '/login' && authenticated) return navigateTo('/dashboard');
  if (to.path !== '/login' && !authenticated) return navigateTo('/login');
  if (to.path === '/usuarios' && user.value?.tipo !== 'ADMIN') return navigateTo('/dashboard');
});
