// Tipos estritos mapeados exatamente conforme o Backend Spring Boot do GYM+

export type StatusAluno = 'ATIVO' | 'INATIVO';
export type StatusMatricula = 'ATIVA' | 'CANCELADA';
export type StatusPlano = 'ATIVO' | 'INATIVO';
export type StatusProfessor = 'ATIVO' | 'INATIVO';
export type StatusTurma = 'ATIVA' | 'INATIVA';
export type StatusUsuario = 'ATIVO' | 'INATIVO';

export type UserRole = 'ADMIN' | 'FUNCIONARIO';

export interface Pessoa {
  nome: string;
  cpf: string;
  telefone: string;
  email: string;
}

export interface Aluno extends Pessoa {
  id?: number;
  dataNascimento: string; // ISO yyyy-MM-dd
  status?: StatusAluno;
}

export interface Professor extends Pessoa {
  id?: number;
  cref: string;
  modalidade: string;
  status?: StatusProfessor;
}

export interface Usuario extends Pessoa {
  id?: number;
  senha?: string;
  status?: StatusUsuario;
}

export interface Plano {
  id?: number;
  nome: string;
  duracao: number; // Em meses
  valorMensal: number;
  status?: StatusPlano;
}

export interface Turma {
  id?: number;
  modalidade: string;
  horario: string; // Ex: '08:00', '19:30'
  maximoVagas: number;
  professor?: Professor;
  matriculas?: Matricula[];
  status?: StatusTurma;
}

export interface Matricula {
  id?: number;
  dataInicio?: string;
  dataFim?: string;
  status?: StatusMatricula;
  motivoCancelamento?: string | null;
  aluno?: Aluno;
  plano?: Plano;
  turma?: Turma;
}

export interface MatriculaRequest {
  idAluno: number;
  idPlano: number;
  idTurma: number;
}

export interface ApiErrorResponse {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  errors?: Array<{
    field: string;
    defaultMessage: string;
  }>;
}
