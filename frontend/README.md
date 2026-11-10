# GYM+ — Frontend

Painel para organizar a rotina da academia. Aqui você pode acompanhar alunos, professores, turmas, planos e matrículas.

## Tecnologias

- Nuxt 4
- Vue 3
- TypeScript
- SCSS

## Como iniciar

1. Instale o Node.js.
2. Abra esta pasta no terminal.
3. Instale as dependências:

```bash
npm install
```

4. Crie um arquivo `.env` com esta configuração:

```env
NUXT_PUBLIC_API_BASE=http://localhost:8080
```

5. Inicie o projeto:

```bash
npm run dev
```

6. Abra `http://localhost:3000`.

O backend Spring Boot precisa estar ligado para carregar e salvar os dados.

## Pastas principais

- `app/pages`: telas do sistema.
- `app/components`: partes reutilizadas das telas.
- `app/composables/useGymApi.ts`: comunicação com o backend.
- `app/types`: tipos dos dados.
- `app/assets/scss/global.scss`: cores, fontes e estilos gerais.

## Importante sobre o login

Esta versão reorganiza a interface e mantém a comunicação com as rotas que já existiam no frontend enviado. A autenticação real (login, token JWT e identificação do usuário conectado) ainda precisa ser ligada às rotas e ao formato de resposta do backend final. O perfil mostrado no cabeçalho ainda é temporário; não use isso como proteção de segurança. As permissões precisam ser validadas pelo backend.
