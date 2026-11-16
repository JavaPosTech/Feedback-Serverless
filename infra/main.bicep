targetScope = 'resourceGroup'

param location string = resourceGroup().location

@allowed([
  'dev'
  'prod'
])
param ambiente string = 'dev'

param nomeBase string = 'feedback'

@minLength(1)
param emailsAdministradores array

param adminSqlLogin string

param adminSqlObjectId string

@allowed([
  'User'
  'Group'
])
param adminSqlTipo string = 'User'

param leitoresObjectIds array = []

param implantadorPrincipalId string = ''

@minValue(0)
param orcamentoMensal int = 20

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

resource orcamento 'Microsoft.Consumption/budgets@2023-11-01' = if (orcamentoMensal > 0) {
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