# ⚙️ Nortte Blog - Backend API

Uma API RESTful moderna, robusta e escalável desenvolvida com **Spring Boot** para o portal Nortte Blog. Este backend fornece gerenciamento completo de publicações, categorias, usuários e controle de acesso baseado em roles.

---

## 🎯 Sobre o Projeto

O **Backend do Nortte Blog** atua como o motor de conteúdo da plataforma, entregando dados para o Frontend (SSG) de maneira rápida e segura.

Características principais:
- ⚡ **Performance**: Consultas otimizadas com Hibernate, suporte a paginação e lazy/eager loading adequado.
- 🛡️ **Segurança**: Autenticação Stateless utilizando JWT, sanitização de conteúdo em HTML contra ataques XSS.
- 🗄️ **Banco de Dados**: Modelagem relacional robusta com PostgreSQL e controle de versão através do Flyway.
- 📝 **Conteúdo**: Suporte à conversão e sanitização de conteúdos em Markdown.

---

## 🛠️ Stack Tecnológica

As principais tecnologias e ferramentas que compõem este projeto.

<div style="display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 20px;">
  <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/springboot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens" alt="JWT" />
  <img src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</div>

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| **Spring Boot** | 3.3.0 | Framework Java para desenvolvimento da API REST |
| **Java** | 21 | Linguagem de programação (LTS) |
| **PostgreSQL** | 14+ | Banco de dados relacional |
| **Spring Security** | Integrado | Autenticação, autorização e controle de acessos (RBAC) |
| **Spring Data JPA** | Integrado | Mapeamento Objeto-Relacional (ORM) e persistência |
| **JJWT** | 0.12.5 | Geração e validação de Tokens JWT |
| **Flyway** | Integrado | Controle e versionamento de migrations do banco de dados |
| **CommonMark** | 0.21.0 | Conversor e renderizador de Markdown para HTML |
| **OWASP HTML Sanitizer** | 20240325.1 | Validação de segurança e sanitização do HTML gerado |
| **Gradle** | 8+ | Gerenciador de dependências e automação de build |

---

## 🚀 Setup Local

### Pré-requisitos
Certifique-se de ter instalado:
- **Java 21**
- **Docker & Docker Compose** (opcional, para rodar o PostgreSQL de forma conteinerizada)
- **PostgreSQL 14+** (se não for utilizar o Docker)
- **Git**

### 1. Clonar o Repositório

```bash
git clone <backend-repo-url>
cd abertamente_cms
```

### 2. Configurar Variáveis de Ambiente

Crie o arquivo `.env` na raiz do projeto com as credenciais do banco:

```properties
DB_URL=jdbc:postgresql://localhost:5432/nortteblog
DB_USER=postgres
DB_PASSWORD=sua_senha_aqui
JWT_SECRET=sua_chave_secreta_muito_longa_e_segura
```

### 3. Rodar a Aplicação

**Opção A: Com Docker Compose (Recomendado)**
```bash
# Iniciar o banco de dados PostgreSQL
docker-compose up -d

# Compilar e rodar a aplicação localmente
./gradlew bootRun
```

**Opção B: Com PostgreSQL Local**
Crie um banco de dados vazio chamado `nortteblog` e depois inicie a aplicação.
```bash
createdb nortteblog
./gradlew bootRun
```
> *Nota: As migrações do banco (tabelas e estrutura) são executadas automaticamente pelo Flyway durante a inicialização.*

O backend estará acessível em: `http://localhost:8080`

### 4. Validar Funcionamento
Verifique se a aplicação subiu corretamente:
```bash
curl http://localhost:8080/api/health
```

---

## 📁 Estrutura do Projeto

A arquitetura do backend segue uma separação rigorosa em camadas e diretórios baseada em princípios estruturados:

```text
src/main/java/com_abertamente_cms/
├── controller/         # Camada de apresentação: Endpoints REST
├── service/            # Camada de regras de negócios
├── repository/         # Acesso a dados via Spring Data JPA
├── domain/             # Entidades de banco de dados e Enums
├── dto/                # Data Transfer Objects (Inputs/Outputs)
├── security/           # Políticas de segurança e JWT
├── exception/          # Exceções personalizadas e Handler global (@ControllerAdvice)
└── config/             # Configurações do Spring Boot
```

---

## 🔧 Comandos Úteis

No terminal raiz do projeto backend, utilize o Gradle Wrapper para gerenciar a aplicação:

```bash
# Executar servidor
./gradlew bootRun

# Rodar todos os testes automatizados
./gradlew test

# Empacotar para produção (gera o arquivo JAR em build/libs)
./gradlew build
```

---

## 📊 Critérios de Qualidade

- ✅ **Segurança**: Nenhuma credencial exportada (`.env`), senhas *hashadas* com BCrypt e acessos validados state-less (JWT).
- ✅ **Design Padrão**: As entidades (`Entities`) jamais transitam na resposta Web; utilizamos Data Transfer Objects (`DTOs`).
- ✅ **Testes Automatizados**: TDD levado a sério. Serviços cruciais, controladores e validações possuem ampla cobertura com JUnit e Mockito.
- ✅ **Performance de Dados**: Queries e relatórios com acesso eficiente sem sobrecarregar a memória, evitando problema de N+1 Queries.

---

## 🤝 Contribuindo

1. Crie uma nova branch para a funcionalidade (`git checkout -b feature/minha-feature`).
2. Adicione os novos arquivos e implemente testes automatizados correspondentes para qualquer código que você escrever.
3. Se for integrar novos fluxos, rode `/review-before-commit` e certifique-se que o código cumpre os padrões arquiteturais do backend.
4. Realize o commit do seu código com comentários explicativos.
5. Crie um Pull Request.
