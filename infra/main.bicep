targetScope = 'resourceGroup'

@description('Região dos recursos regionais.')
param location string = resourceGroup().location

@description('Nome do ambiente, usado nos nomes e nas tags.')
@allowed([
  'dev'
  'prod'
])
param ambiente string = 'dev'

@description('Prefixo dos nomes dos recursos.')
param nomeBase string = 'feedback'

@description('E-mails que recebem as notificações de urgência, o relatório semanal, os alertas e o aviso de orçamento.')
@minLength(1)
param emailsAdministradores array

@description('Login (UPN ou nome do grupo) do administrador Entra ID do SQL Server.')
param adminSqlLogin string

@description('Object ID do administrador Entra ID do SQL Server.')
param adminSqlObjectId string

@description('Tipo do administrador do SQL Server.')
@allowed([
  'User'
  'Group'
])
param adminSqlTipo string = 'User'

@description('Object IDs dos membros do grupo que recebem acesso de leitura ao resource group.')
param leitoresObjectIds array = []

@description('Object ID da identidade usada pelo GitHub Actions para publicar as funções. Vazio para não atribuir.')
param implantadorPrincipalId string = ''

@description('Valor mensal do orçamento, na moeda da assinatura.')
param orcamentoMensal int = 20

@description('Primeiro dia do mês em que o orçamento começa a valer.')
param inicioOrcamento string = utcNow('yyyy-MM-01')

var sufixo = take(uniqueString(resourceGroup().id), 6)
var nomeFunctionApp = 'func-${nomeBase}-${ambiente}-${sufixo}'
var tags = {
  projeto: 'Feedback-Serverless'
  ambiente: ambiente
  curso: 'FIAP Tech Challenge Fase 4'
}

module monitoramento 'modules/monitoring.bicep' = {
  name: 'monitoramento'
  params: {
    location: location
    nome: '${nomeBase}-${ambiente}'
    emails: emailsAdministradores
    tags: tags
  }
}

module armazenamento 'modules/storage.bicep' = {
  name: 'armazenamento'
  params: {
    location: location
    nome: 'st${nomeBase}${ambiente}${sufixo}'
    tags: tags
  }
}

module banco 'modules/sql.bicep' = {
  name: 'banco'
  params: {
    location: location
    nome: 'sql-${nomeBase}-${ambiente}-${sufixo}'
    adminLogin: adminSqlLogin
    adminObjectId: adminSqlObjectId
    adminTipo: adminSqlTipo
    tags: tags
  }
}

module email 'modules/email.bicep' = {
  name: 'email'
  params: {
    nome: '${nomeBase}-${ambiente}-${sufixo}'
    tags: tags
  }
}

module cofre 'modules/keyvault.bicep' = {
  name: 'cofre'
  params: {
    location: location
    nome: 'kv-${nomeBase}-${ambiente}-${sufixo}'
    communicationServiceNome: email.outputs.communicationServiceNome
    tags: tags
  }
}

module funcoes 'modules/function-app.bicep' = {
  name: 'funcoes'
  params: {
    location: location
    nome: nomeFunctionApp
    storageNome: armazenamento.outputs.nome
    blobEndpoint: armazenamento.outputs.blobEndpoint
    queueEndpoint: armazenamento.outputs.queueEndpoint
    appInsightsConnectionString: monitoramento.outputs.appInsightsConnectionString
    databaseUrl: 'jdbc:sqlserver://${banco.outputs.fqdn}:1433;database=${banco.outputs.databaseNome};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;authentication=ActiveDirectoryMSI'
    emailConnectionStringSecretUri: cofre.outputs.segredoEmailUri
    emailRemetente: email.outputs.remetente
    emailsAdministradores: emailsAdministradores
    tags: tags
  }
}

module permissoes 'modules/rbac.bicep' = {
  name: 'permissoes'
  params: {
    functionAppNome: funcoes.outputs.nome
    functionAppPrincipalId: funcoes.outputs.principalId
    storageNome: armazenamento.outputs.nome
    cofreNome: cofre.outputs.nome
    leitoresObjectIds: leitoresObjectIds
    implantadorPrincipalId: implantadorPrincipalId
  }
}

resource orcamento 'Microsoft.Consumption/budgets@2023-11-01' = {
  name: 'orcamento-${nomeBase}-${ambiente}'
  properties: {
    category: 'Cost'
    amount: orcamentoMensal
    timeGrain: 'Monthly'
    timePeriod: {
      startDate: inicioOrcamento
    }
    notifications: {
      gastoReal80: {
        enabled: true
        operator: 'GreaterThan'
        threshold: 80
        thresholdType: 'Actual'
        contactEmails: emailsAdministradores
      }
      previsao100: {
        enabled: true
        operator: 'GreaterThan'
        threshold: 100
        thresholdType: 'Forecasted'
        contactEmails: emailsAdministradores
      }
    }
  }
}

output functionAppNome string = funcoes.outputs.nome
output functionAppUrl string = 'https://${funcoes.outputs.hostName}'
output sqlServidor string = banco.outputs.fqdn
output sqlBanco string = banco.outputs.databaseNome
output emailRemetente string = email.outputs.remetente