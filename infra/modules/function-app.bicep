param location string
param nome string
param storageNome string
param blobEndpoint string
param queueEndpoint string
param appInsightsConnectionString string
param databaseUrl string
param emailConnectionStringSecretUri string
param emailRemetente string
param emailsAdministradores array
param tags object

resource plano 'Microsoft.Web/serverfarms@2024-04-01' = {
  name: 'asp-${nome}'
  location: location
  tags: tags
  kind: 'functionapp'
  sku: {
    name: 'FC1'
    tier: 'FlexConsumption'
  }
  properties: {
    reserved: true
  }
}

resource functionApp 'Microsoft.Web/sites@2024-04-01' = {
  name: nome
  location: location
  tags: tags
  kind: 'functionapp,linux'
  identity: {
    type: 'SystemAssigned'
  }
  properties: {
    serverFarmId: plano.id
    httpsOnly: true
    siteConfig: {
      minTlsVersion: '1.2'
      ftpsState: 'Disabled'
      appSettings: [
        {
          name: 'AzureWebJobsStorage__accountName'
          value: storageNome
        }
        {
          name: 'APPLICATIONINSIGHTS_CONNECTION_STRING'
          value: appInsightsConnectionString
        }
        {
          name: 'DATABASE_URL'
          value: databaseUrl
        }
        {
          name: 'FILA_ENDPOINT'
          value: queueEndpoint
        }
        {
          name: 'EMAIL_CONNECTION_STRING'
          value: '@Microsoft.KeyVault(SecretUri=${emailConnectionStringSecretUri})'
        }
        {
          name: 'EMAIL_REMETENTE'
          value: emailRemetente
        }
        {
          name: 'EMAIL_DESTINATARIOS'
          value: join(emailsAdministradores, ',')
        }
      ]
    }
    functionAppConfig: {
      deployment: {
        storage: {
          type: 'blobContainer'
          value: '${blobEndpoint}deployments'
          authentication: {
            type: 'SystemAssignedIdentity'
          }
        }
      }
      scaleAndConcurrency: {
        maximumInstanceCount: 40
        instanceMemoryMB: 2048
      }
      runtime: {
        name: 'java'
        version: '21'
      }
    }
  }
}

resource semFtp 'Microsoft.Web/sites/basicPublishingCredentialsPolicies@2024-04-01' = {
  parent: functionApp
  name: 'ftp'
  properties: {
    allow: false
  }
}

resource semScm 'Microsoft.Web/sites/basicPublishingCredentialsPolicies@2024-04-01' = {
  parent: functionApp
  name: 'scm'
  properties: {
    allow: false
  }
}

output nome string = functionApp.name
output hostName string = functionApp.properties.defaultHostName
output principalId string = functionApp.identity.principalId