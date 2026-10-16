<div align="center"> <br>
  <img align="center" alt="feedback-serverless-quarkus" height="150" width="150" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/quarkus/quarkus-original.svg" />
</div>

<br>

<div align="center">
  Plataforma de feedback de aulas construída com funções serverless na Azure. Os estudantes avaliam as aulas, os administradores recebem um e-mail quando uma avaliação é crítica e, toda semana, um relatório com a média e a distribuição das avaliações. Projeto do Tech Challenge da Fase 4 da FIAP.
</div>

<br> <br>

## 🚀 Ferramentas Utilizadas

* 🐳 Docker

* 🕊️ Flyway

* 🧱 Azurite

* ☕️ Java 21

* 🐘 Gradle 9.8.0

* ⚡ Quarkus 3.40.1

* ☁️ Azure Functions

* 🗄️ SQL Server 2022

<br>

## ⚙️ Pré-requisitos

* JDK 21 instalada (o Gradle Wrapper baixa o Gradle sozinho)

* Docker, para subir o SQL Server e o Azurite

<br>

## ▶️ Como Executar

```bash
# Sobe o SQL Server (porta 1433, cria os bancos feedback e feedback_test) e o Azurite (portas 10000 a 10002)
docker compose up -d --wait
```

A senha local do SQL Server pode ser trocada criando um `.env` a partir do `.env.example`.