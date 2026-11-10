<template>
  <BaseModal
    :model-value="modelValue"
    :title="title"
    size="sm"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="$emit('cancel')"
  >
    <div style="display: flex; flex-direction: column; gap: 1rem;">
      <p style="color: var(--text-secondary); font-size: 0.95rem; line-height: 1.5;">
        {{ message }}
      </p>

      <!-- Campo opcional ou obrigatório de motivo (ex: Cancelamento de Matrícula) -->
      <div v-if="requireReason" class="form-group">
        <label class="form-label">
          Motivo da Operação <span class="required">*</span>
        </label>
        <textarea
          v-model="reasonText"
          class="form-control"
          :class="{ 'has-error': reasonError }"
          rows="3"
          :placeholder="reasonPlaceholder || 'Descreva detalhadamente o motivo...'"
        ></textarea>
        <span v-if="reasonError" class="form-error">{{ reasonError }}</span>
      </div>

      <div style="background-color: var(--danger-bg); border-radius: var(--radius-md); padding: 0.75rem 1rem; display: flex; gap: 0.5rem; align-items: center; color: var(--danger-text); font-size: 0.85rem;">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"></path>
          <line x1="12" y1="9" x2="12" y2="13"></line>
          <line x1="12" y1="17" x2="12.01" y2="17"></line>
        </svg>
        <span>Esta alteração é lógica e será persistida no banco de dados.</span>
      </div>
    </div>

    <template #footer>
      <button
        type="button"
        class="btn btn-secondary btn-sm"
        :disabled="loading"
        @click="handleCancel"
      >
        Cancelar
      </button>
      <button
        type="button"
        class="btn btn-danger btn-sm"
        :disabled="loading"
        @click.stop.prevent="handleConfirm"
      >
        <span v-if="loading" class="spinner" style="width: 14px; height: 14px; border-width: 2px;"></span>
        {{ confirmButtonText || 'Confirmar' }}
      </button>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import BaseModal from './BaseModal.vue';

const props = withDefaults(
  defineProps<{
    modelValue: boolean;
    title: string;
    message: string;
    confirmButtonText?: string;
    requireReason?: boolean;
    reasonPlaceholder?: string;
    loading?: boolean;
  }>(),
  {
    confirmButtonText: 'Confirmar',
    requireReason: false,
    loading: false
  }
);

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'confirm', reason?: string): void;
  (e: 'cancel'): void;
}>();

const reasonText = ref('');
const reasonError = ref('');

watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    reasonText.value = '';
    reasonError.value = '';
  }
});

const handleCancel = () => {
  emit('update:modelValue', false);
  emit('cancel');
};

const handleConfirm = () => {
  if (props.requireReason) {
    if (!reasonText.value.trim()) {
      reasonError.value = 'O preenchimento do motivo é obrigatório para esta operação.';
      return;
    }
  }
  emit('confirm', reasonText.value.trim());
};
</script>
