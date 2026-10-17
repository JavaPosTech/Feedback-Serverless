<div align="center"> <br>
  <img align="center" alt="feedback-serverless-quarkus" height="150" width="150" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/quarkus/quarkus-original.svg" />
</div>

<br>

<div align="center">
  Plataforma de feedback de aulas construída com funções serverless na Azure. Os estudantes avaliam as aulas, os administradores recebem um e-mail quando uma avaliação é crítica e, toda semana, um relatório com a média e a distribuição das avaliações. Projeto do Tech Challenge da Fase 4 da FIAP.
</div>

<br> <br>

## 🚀 Ferramentas Utilizadas

* 📨 Qute

* 🔷 Bicep

* 🐳 Docker

* 🕊️ Flyway

* 🔴 Lombok

* 🧱 Azurite

* ☕️ Java 21

* 🧪 JUnit 5

* 🗺️ MapStruct

* 🐘 Gradle 9.8.0

* ⚡ Quarkus 3.40.1

* ⚙️ GitHub Actions

* 🔐 Azure Key Vault

* ☁️ Azure Functions

* 🗄️ SQL Server 2022

* 📬 Azure Storage Queue

* 📈 Application Insights

* 🛢️ Azure SQL Database (serverless)

* ✉️ Azure Communication Services (Email)

<br>

## 🎯 Objetivo

Receber avaliações de aulas, avisar os administradores sobre problemas críticos assim que eles acontecem e entregar um relatório semanal para acompanhar a satisfação dos estudantes, sem manter nenhum servidor ligado. A solução roda inteira no modelo serverless: cada função só executa quando o seu gatilho acontece e o banco pausa sozinho quando ninguém usa.

<br>

## 🔎 Como Funciona

O projeto é **um repositório e um Function App com três funções**. Cada função tem um gatilho e uma única responsabilidade:

| Função | Gatilho | Responsabilidade |
|---|---|---|
| ⚡ **ReceberAvaliacao** | HTTP `POST /avaliacao` | Valida, calcula a urgência, grava no banco e, se a avaliação for crítica, publica na fila |
| 📬 **NotificarUrgencia** | Mensagem na fila `avaliacoes-criticas` | Envia aos administradores o e-mail com descrição, urgência e data de envio |
| 📊 **GerarRelatorio** | Timer `0 0 11 * * 1` (segunda, 08:00 de Brasília) | Calcula média, quantidade por dia e por urgência dos últimos 7 dias e envia o relatório |

<img alt="arquitetura" src="arquitetura.png" />

* **Por que uma fila entre a entrada e a notificação:** o estudante recebe a resposta na hora, uma falha no envio do e-mail não perde a avaliação e a fila tenta de novo sozinha (5 tentativas, depois a mensagem vai para `avaliacoes-criticas-poison`).

* **Regra de urgência:** fica em `Urgencia.deNota` e é a única regra que decide o fluxo.

| Nota | Urgência | Efeito |
|---|---|---|
| 0 a 3 | Crítica | E-mail imediato via fila |
| 4 a 6 | Média | Entra só no relatório |
| 7 a 10 | Baixa | Entra só no relatório |

<br>

## 🔌 API

### `POST /avaliacao`

Protegido por chave de função: envie `?code=<chave>` ou o cabeçalho `x-functions-key`.

```json
{
  "nota": 2,
  "descricao": "O áudio cortou várias vezes durante a aula"
}
```

| Status | Quando |
|---|---|
| `201 Created` | Avaliação gravada. O corpo traz `id`, `nota`, `descricao`, `urgencia` e `dataEnvio` |
| `400 Bad Request` | JSON inválido, nota ausente ou fora de 0 a 10, descrição vazia ou acima de 500 caracteres |
| `500 Internal Server Error` | Falha inesperada, registrada no Application Insights |

Os erros seguem um único formato (`status`, `title`, `instance`, `type`, `detail`, `errors`, `timestamp`), montado por `FunctionExceptionHandler`.

<br>

## ⚙️ Pré-requisitos

* JDK 21 instalada (o Gradle Wrapper baixa o Gradle sozinho)

* Docker, para subir o SQL Server e o Azurite usados nos testes

* Azure CLI e uma assinatura Azure, apenas para publicar

* Azure Functions Core Tools 4 (`npm install -g azure-functions-core-tools@4`), para rodar as funções localmente e publicar na Azure

<br>

## 🔐 Variáveis de Ambiente

Em produção, todas são criadas pelo Bicep como configurações do Function App:

| Variável | Descrição |
|---|---|
| `AzureWebJobsStorage__accountName` | Conta de storage usada pelo runtime e pela fila, acessada por identidade gerenciada |
| `APPLICATIONINSIGHTS_CONNECTION_STRING` | Destino dos logs, métricas e traces |
| `DATABASE_URL` | JDBC do Azure SQL com `authentication=ActiveDirectoryMSI` (sem senha) |
| `FILA_ENDPOINT` | Endpoint da Storage Queue, acessado por identidade gerenciada |
| `EMAIL_CONNECTION_STRING` | Referência ao segredo do Key Vault com a connection string do Communication Services |
| `EMAIL_REMETENTE` | Remetente do domínio gerenciado pela Azure |
| `EMAIL_DESTINATARIOS` | E-mails dos administradores, separados por vírgula |

Nos perfis `dev` e `test`, o banco aponta para o SQL Server do `docker-compose.yml`, a fila para o Azurite e o envio de e-mail é simulado no log. A senha local pode ser trocada criando um `.env` a partir do `.env.example`.

<br>

## ▶️ Como Executar

🔹 Serviços locais

```bash
# Sobe o SQL Server (porta 1433, cria os bancos feedback e feedback_test) e o Azurite (portas 10000 a 10002)
docker compose up -d --wait
```

🔹 Perfis do IntelliJ

A pasta `.run` traz os perfis prontos, que aparecem no seletor de execução do IntelliJ:

| Perfil | Comando | O que faz |
|---|---|---|
| 🟢 **QuarkusRun - DEV** | `quarkusRun` | Gera o pacote e sobe as três funções com o perfil `dev`: banco `feedback` local, fila no Azurite e e-mail simulado no log |
| 🔴 **QuarkusRun - PROD** | `quarkusRun` | Sobe as funções com o perfil `prod`, a mesma configuração da Azure, apontando para o SQL Server e o Azurite locais e com o e-mail simulado |
| 🧱 **Clean Build - [Without Tests]** | `clean build -x test` | Gera o pacote em `build/azure-functions` sem rodar os testes |
| 🧪 **Testes de Integração** | `:test` | Roda os testes contra o banco `feedback_test` e a fila `avaliacoes-criticas-teste` |

🔹 Funções pelo terminal

```bash
# Copia as configurações locais do runtime de Functions (perfil dev)
cp local.settings.example.json local.settings.json

# Gera o pacote e inicia as três funções com o Azure Functions Core Tools
./gradlew quarkusRun
```

Com as funções rodando, a avaliação é enviada para `http://localhost:8081/avaliacao`. As variáveis definidas no ambiente têm prioridade sobre o `local.settings.json`, e é assim que os perfis do IntelliJ escolhem entre `dev` e `prod`.

<br>

## 🧪 Testes e Build

```bash
# Testes de integração contra o SQL Server e o Azurite do docker-compose
./gradlew test

# Roda os testes e gera o pacote para a Azure em build/azure-functions/<nome-do-function-app>
./gradlew build
```

Cada teste sobe o contexto completo do Quarkus, roda contra o banco `feedback_test` populado por `db/test/V2__PopularBanco.sql` e desfaz as alterações ao final com `@TestTransaction`. Os testes usam banco e fila próprios para não misturar dados com o perfil `dev`.

<br>

## ☁️ Deploy na Azure

🔹 1. Criar a infraestrutura

```bash
# Cria o resource group
az group create --name rg-feedback-dev --location brazilsouth

# Cria Function App, banco, fila, e-mail, Key Vault, monitoramento, permissões e orçamento
az deployment group create --resource-group rg-feedback-dev --template-file infra/main.bicep --parameters infra/dev.bicepparam
```

Antes, preencha `infra/dev.bicepparam` com os e-mails dos administradores e o administrador Entra ID do SQL (`az ad signed-in-user show --query "{login:userPrincipalName, id:id}"`).

🔹 2. Dar acesso da função ao banco

No Query editor do banco `feedback`, entrando com o administrador Entra ID, troque o nome pelo do Function App criado:

```sql
CREATE USER [func-feedback-dev-xxxxxx] FROM EXTERNAL PROVIDER;
ALTER ROLE db_datareader ADD MEMBER [func-feedback-dev-xxxxxx];
ALTER ROLE db_datawriter ADD MEMBER [func-feedback-dev-xxxxxx];
ALTER ROLE db_ddladmin ADD MEMBER [func-feedback-dev-xxxxxx];
```

🔹 3. Publicar e testar

```bash
# Gera o pacote em build/azure-functions/<function-app>
FUNCTION_APP_NAME=<function-app> ./gradlew build

# Publica as três funções com o Azure Functions Core Tools
cd build/azure-functions/<function-app> && func azure functionapp publish <function-app>

# Busca a chave da função de entrada
az functionapp function keys list --resource-group rg-feedback-dev --name <function-app> --function-name ReceberAvaliacao

# Envia uma avaliação crítica, que dispara o e-mail de urgência
curl -X POST "https://<function-app>.azurewebsites.net/avaliacao?code=<chave>" -H "Content-Type: application/json" -d '{"nota":1,"descricao":"O vídeo não carrega"}'

# Dispara o relatório semanal na hora, sem esperar a segunda-feira
curl -X POST "https://<function-app>.azurewebsites.net/admin/functions/GerarRelatorio" -H "x-functions-key: <master-key>" -H "Content-Type: application/json" -d '{}'
```

A `master-key` sai de `az functionapp keys list`.

<br>

## 📈 Monitoramento

O Application Insights recebe logs, métricas e a execução de cada função. Dois alertas avisam os administradores por e-mail:

| Alerta | Condição | Severidade |
|---|---|---|
| Execuções de função com falha | Alguma execução terminou com erro nos últimos 5 minutos | 1 |
| Exceções na aplicação | Mais de 5 exceções em 15 minutos | 2 |

<br>

## 🛡️ Segurança e Governança

* **Sem senhas no código:** a função acessa banco, fila e Key Vault com identidade gerenciada.

* **Único segredo no Key Vault:** a connection string do Communication Services, lida pela função por referência.

* **Menor privilégio:** cada identidade recebe só os papéis de que precisa, e os membros do grupo entram como leitores.

* **Transporte protegido:** HTTPS obrigatório, TLS 1.2 mínimo, FTP e publicação por senha desativados, storage sem acesso público e sem chave compartilhada.

* **Governança de custo:** tags em todos os recursos e orçamento mensal com aviso em 80% do gasto real e 100% da previsão.

<br>

## 🤖 Integração Contínua

| Workflow | Gatilho | O que faz |
|---|---|---|
| `ci.yml` | Pull Request para `main` | Sobe SQL Server e Azurite e roda `./gradlew build` |

<br>

## 📁 Estrutura

```
src/main/java/br/com/fiap/feedback
├── function                    # As três funções: um gatilho por classe
├── service                     # Regras de negócio: avaliação, notificação e relatório
├── repository                  # Repositório Panache e consultas do relatório
├── client                      # Integrações com a fila e o e-mail da Azure
├── template                    # Templates Qute dos e-mails e formatação
├── model                       # dto, entity e mapper (MapStruct)
├── enums                       # Urgencia e a regra de classificação pela nota
├── exceptions                  # Exceções, dto e FunctionExceptionHandler
└── config                      # FeedbackConfig e clientes do SDK da Azure

src/main/resources
├── application.properties      # Configuração por perfil (dev, test e produção)
├── db/migration                # Migrations do Flyway
└── templates/email             # HTML dos e-mails de urgência e do relatório

infra
├── main.bicep                  # Orquestra os módulos, o orçamento e as saídas
├── dev.bicepparam              # Parâmetros do ambiente dev
└── modules                     # function-app, sql, storage, email, keyvault, monitoring e rbac

src/test/java/br/com/fiap/feedback
├── config                      # Classes base e dublês de requisição HTTP
├── function                    # Um teste por função
├── service                     # Um teste por método público de service
├── repository                  # Um teste por consulta do repositório
└── enums                       # Limites da regra de urgência
```

<br>

## ⚠️ Limitações

* A infraestrutura foi validada com `bicep build`, mas não foi publicada numa assinatura a partir deste repositório. Confira se a região escolhida oferece o plano Flex Consumption.

* Se a publicação na fila falhar depois de gravar a avaliação, a função responde 500 e a avaliação fica salva sem notificação. Um reenvio do cliente gera uma segunda avaliação.

* O relatório lista todas as avaliações da semana no corpo do e-mail. Para volumes grandes, o ideal seria um anexo ou um link.

* As datas são gravadas no horário de Brasília (`feedback.fuso-horario`), para que o relatório agrupe pelos dias que os administradores enxergam.