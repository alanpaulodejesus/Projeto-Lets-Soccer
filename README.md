<div align="center">

# ⚽ Lets Soccer

**Um projeto para quem ama futebol**

API desenvolvida com Java 17 e Spring Boot, com autenticação JWT, persistência em PostgreSQL, testes BDD com Cucumber e execução via Docker.

<br>

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Cucumber](https://img.shields.io/badge/Cucumber-23D96C?style=for-the-badge&logo=cucumber&logoColor=white)

</div>

---

## 📑 Sumário

- [Sobre o projeto](#-sobre-o-projeto)
- [Tecnologias](#️-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Como executar](#️-como-executar)
- [Testes](#-testes)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Contribuição](#-contribuição)
- [Autor](#-autor)

---

## ⚽ Sobre o projeto

O **Lets Soccer** é um projeto criado para os apaixonados por futebol. Trata-se de uma aplicação back-end construída com **Spring Boot**, que conta com:

- 🔐 **Autenticação e autorização** com Spring Security e **JWT**
- 🗄️ **Persistência de dados** com Spring Data JPA (PostgreSQL)
- ✅ **Validação de dados** de entrada com Bean Validation
- 📧 **Envio de e-mails** com Spring Mail
- 🥒 **Testes BDD** com Cucumber
- 🐳 **Execução containerizada** com Docker e Docker Compose

---

## 🛠️ Tecnologias

| Categoria | Tecnologia |
|-----------|------------|
| **Linguagem** | ☕ Java 17 |
| **Framework** | 🍃 Spring Boot 3.2.5 (Web, Data JPA, Validation, Mail) |
| **Segurança** | 🔐 Spring Security + JWT (jjwt 0.11.5) |
| **Banco de dados** | 🐘 PostgreSQL 16 · 🧪 H2 |
| **Build** | 📦 Maven (com Maven Wrapper) |
| **Produtividade** | 🌶️ Lombok |
| **Testes** | 🥒 Cucumber 7.15.0 (Java, Spring e JUnit Platform) · Spring Boot Test |
| **Infraestrutura** | 🐳 Docker e Docker Compose |

---

## ✅ Pré-requisitos

Escolha uma das formas de execução e tenha instalado:

**🐳 Com Docker (recomendado)**
- [Docker](https://www.docker.com/get-started/)
- [Docker Compose](https://docs.docker.com/compose/)

**💻 Localmente**
- [Java JDK 17](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi) (ou use o `mvnw` incluso no projeto)
- [PostgreSQL 16](https://www.postgresql.org/download/) (caso utilize o perfil `postgres`)

---

## ▶️ Como executar

### 1️⃣ Clone o repositório

```bash
git clone https://github.com/alanpaulodejesus/Projeto-Lets-Soccer.git
cd Projeto-Lets-Soccer
```

### 2️⃣ Execute com Docker Compose 🐳

Sobe a aplicação e o banco PostgreSQL com um único comando:

```bash
docker compose up --build
```

| Serviço | Container | Porta de acesso |
|---------|-----------|:---------------:|
| 🚀 **Aplicação** | `letssoccer-app` | `8081` |
| 🐘 **PostgreSQL** | `letssoccer-postgres` | `5432` |

A aplicação ficará disponível em **http://localhost:8081**.

<details>
<summary>⚙️ <b>Configuração do banco no Docker Compose</b></summary>

<br>

| Variável | Valor |
|----------|-------|
| `POSTGRES_DB` | `letssoccer` |
| `POSTGRES_USER` | `postgres` |
| `POSTGRES_PASSWORD` | `postgres` |
| `SPRING_PROFILES_ACTIVE` | `postgres` |
| `DB_URL` | `jdbc:postgresql://postgres:5432/letssoccer` |

Os dados do banco são persistidos no volume `postgres-data`.

> ⚠️ Essas credenciais são apenas para ambiente de desenvolvimento. Altere-as antes de qualquer uso em produção.

</details>

Para encerrar os containers:

```bash
docker compose down
```

### 3️⃣ Ou execute localmente 💻

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Para executar com o perfil do PostgreSQL:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

---

## 🧪 Testes

Para executar a suíte de testes (incluindo os cenários **BDD com Cucumber**):

```bash
./mvnw test
```

---

## 📁 Estrutura do projeto

```
Projeto-Lets-Soccer
├── src
│   ├── main        # Código-fonte da aplicação
│   └── test        # Testes automatizados (Cucumber / Spring Boot Test)
├── Dockerfile      # Imagem da aplicação
├── docker-compose.yml  # Aplicação + PostgreSQL
├── mvnw / mvnw.cmd # Maven Wrapper
└── pom.xml         # Dependências e build
```

---

## 🤝 Contribuição

Contribuições são muito bem-vindas! 💚

1. Faça um **fork** do projeto
2. Crie uma branch: `git checkout -b feature/minha-feature`
3. Faça o commit: `git commit -m "feat: minha feature"`
4. Envie para o repositório: `git push origin feature/minha-feature`
5. Abra um **Pull Request**

---

## 👨‍💻 Autor

Feito por **[alanpaulodejesus](https://github.com/alanpaulodejesus)**

<div align="center">

⚽ Se este projeto te ajudou, deixe uma ⭐ no repositório!

</div>
