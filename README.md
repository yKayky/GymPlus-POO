# GYM+ — Sistema de Gestão de Academia

Aplicação web para gerenciar alunos, professores, planos, turmas e matrículas. O backend utiliza Java e Spring Boot com banco Oracle, e o frontend utiliza Nuxt e Vue.

## Pré-requisitos

- JDK 21, com `JAVA_HOME` configurado para a instalação do JDK.
- Node.js 24.15.0 ou superior na linha 24, ou 22.22.3 ou superior na linha 22, com npm.
- Oracle Database iniciado e um usuário/schema criado para a aplicação, com permissão para criar e alterar suas tabelas.
- Acesso à internet na primeira execução para baixar as dependências.

Confira as versões no PowerShell.

```powershell
java -version
node -v
npm.cmd -v
```

Os comandos abaixo são para Windows com PowerShell. Abra dois terminais na pasta principal do projeto, um para o backend e outro para o frontend.

## 1. Configurar o banco de dados

A conexão padrão está definida em `backend/src/main/resources/application.properties`.

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/FREEPDB1
```

Esse endereço corresponde ao Oracle na máquina local, na porta `1521`, com o serviço `FREEPDB1`. Ajuste a URL caso sua instalação utilize outro endereço, porta ou serviço.

O Hibernate cria ou atualiza as tabelas ao iniciar o backend, conforme a configuração `spring.jpa.hibernate.ddl-auto=update`. O usuário/schema do Oracle deve existir antes dessa etapa.

## 2. Iniciar o backend

No primeiro terminal, entre na pasta do backend.

```powershell
cd .\backend
```

Configure as variáveis abaixo no mesmo terminal, substituindo os valores de exemplo pelos dados do seu ambiente. O comando de `JWT_SECRET` gera uma chave aleatória para assinar os tokens de acesso.

```powershell
$env:DB_USERNAME = "GYMPLUS"
$env:DB_PASSWORD = "SUA_SENHA_DO_ORACLE"

$env:JWT_SECRET = [guid]::NewGuid().ToString('N') + [guid]::NewGuid().ToString('N')

$env:ADMIN_NOME = "Nome do administrador"
$env:ADMIN_CPF = "CPF_DO_ADMINISTRADOR"
$env:ADMIN_TELEFONE = "TELEFONE_DO_ADMINISTRADOR"
$env:ADMIN_EMAIL = "admin@academia.com"
$env:ADMIN_SENHA = "SUA_SENHA_DO_ADMINISTRADOR"

.\mvnw.cmd spring-boot:run
```

O Maven Wrapper baixa o Maven e as dependências necessárias na primeira execução. Aguarde a mensagem de inicialização do Spring Boot e mantenha o terminal aberto.

O backend fica disponível em `http://localhost:8080`.

### Administrador inicial

Na primeira inicialização, o sistema cria o administrador com os dados informados nas variáveis `ADMIN_*`. Quando o e-mail já existe no banco, o sistema utiliza a conta cadastrada. Para entrar, use a senha que está salva nessa conta.

`ADMIN_EMAIL` e `ADMIN_SENHA` precisam estar preenchidos em cada inicialização. Nome, CPF e telefone são exigidos quando a conta inicial precisa ser criada.

As variáveis definidas com `$env:` valem para o terminal atual. Ao abrir outro terminal, configure-as novamente antes de iniciar o backend. A chave `JWT_SECRET` deve ter pelo menos 32 caracteres. Se gerar outra chave, entre novamente no sistema para obter um token válido.

## 3. Iniciar o frontend

No segundo terminal, partindo da pasta principal do projeto, entre na pasta do frontend e instale as dependências.

```powershell
cd .\frontend
npm.cmd ci
```

Inicie o servidor de desenvolvimento.

```powershell
npm.cmd run dev
```

Mantenha esse terminal aberto e acesse `http://localhost:3000/login`. Entre com o e-mail e a senha do administrador cadastrado.

Nas próximas execuções, use `npm.cmd run dev`. Execute `npm.cmd ci` novamente quando precisar instalar ou reinstalar as dependências, inclusive após alterações no arquivo `package-lock.json`.

### Endereço da API

O frontend utiliza `http://localhost:8080` como endereço padrão do backend. Para configurar outro endereço, copie o arquivo de exemplo, caso ainda precise criar o arquivo `.env`.

```powershell
Copy-Item .env.example .env
```

Edite `frontend/.env` com o endereço desejado e reinicie o frontend.

```dotenv
NUXT_PUBLIC_API_BASE=http://localhost:8080
```

O backend permite requisições do frontend na origem `http://localhost:3000`. Ao usar outra origem para o frontend, ajuste a configuração de CORS em `backend/src/main/java/gym_plus/config/SecurityConfig.java`.

## 4. Encerrar o projeto

Pressione `Ctrl + C` no terminal do frontend e no terminal do backend.

## Problemas ao iniciar

| Problema | O que conferir |
| --- | --- |
| Erro de conexão com o Oracle | Verifique se o banco e o serviço estão iniciados, se a URL está correta e se `DB_USERNAME` e `DB_PASSWORD` correspondem ao usuário/schema. |
| Erro sobre variáveis ou administrador inicial | Configure as variáveis no mesmo terminal em que executa o backend. Preencha todos os dados `ADMIN_*` para criar o primeiro administrador. |
| Erro de compilação relacionado à versão do Java | Confira `java -version` e a instalação apontada por `JAVA_HOME`. O projeto utiliza Java 21. |
| Erro de versão do Node.js | Confira `node -v` e utilize uma das versões indicadas nos pré-requisitos. |
| Porta `8080` ou `3000` ocupada | Encerre a execução anterior do projeto ou o processo que está usando a porta e inicie novamente. |
| Frontend abre, mas as chamadas à API falham | Confira se o backend iniciou, se `NUXT_PUBLIC_API_BASE` aponta para ele e se o frontend está em `http://localhost:3000`. |
| Login com senha incorreta após alterar `ADMIN_SENHA` | Use a senha da conta existente no banco. A configuração do administrador inicial cria a conta quando o e-mail ainda não existe. |
