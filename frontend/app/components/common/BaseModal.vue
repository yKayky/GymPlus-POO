<template>
  <Teleport to="body">
    <div
      v-if="modelValue"
      class="modal-backdrop"
      role="dialog"
      aria-modal="true"
      @click.self="handleBackdropClick"
      @keydown.esc="close"
    >
      <div class="modal-dialog" :class="sizeClass">
        <div class="modal-header">
          <div>
            <h3>
              <slot name="icon" />
              {{ title }}
            </h3>
            <p v-if="subtitle" class="form-hint" style="margin-top: 0.2rem;">{{ subtitle }}</p>
          </div>
          <button class="modal-close-btn" @click="close" title="Fechar">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <slot />
        </div>

        <div v-if="$slots.footer" class="modal-footer">
          <slot name="footer" />
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    modelValue: boolean;
    title: string;
    subtitle?: string;
    size?: 'sm' | 'md' | 'lg';
    closeOnBackdrop?: boolean;
  }>(),
  {
    size: 'md',
    closeOnBackdrop: true
  }
);

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'close'): void;
}>();

const sizeClass = computed(() => {
  if (props.size === 'sm') return 'modal-sm';
  if (props.size === 'lg') return 'modal-lg';
  return '';
});

const close = () => {
  emit('update:modelValue', false);
  emit('close');
};

const handleBackdropClick = () => {
  if (props.closeOnBackdrop) {
    close();
  }
};
</script>
