export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title?: string;
  message: string;
  duration?: number;
}

export const useToast = () => {
  const toasts = useState<ToastMessage[]>('gym_toasts', () => []);

  const add = (message: Omit<ToastMessage, 'id'>) => {
    const id = Math.random().toString(36).substring(2, 9);
    const duration = message.duration ?? 4500;
    const newToast: ToastMessage = { ...message, id };

    toasts.value.push(newToast);

    if (duration > 0) {
      setTimeout(() => {
        remove(id);
      }, duration);
    }
  };

  const remove = (id: string) => {
    toasts.value = toasts.value.filter(t => t.id !== id);
  };

  const success = (message: string, title: string = 'Sucesso') => {
    add({ type: 'success', title, message });
  };

  const error = (message: string, title: string = 'Erro') => {
    add({ type: 'error', title, message });
  };

  const warning = (message: string, title: string = 'Atenção') => {
    add({ type: 'warning', title, message });
  };

  const info = (message: string, title: string = 'Informação') => {
    add({ type: 'info', title, message });
  };

  return {
    toasts,
    add,
    remove,
    success,
    error,
    warning,
    info
  };
};
