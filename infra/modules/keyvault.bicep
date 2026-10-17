param location string
param nome string
param communicationServiceNome string
param tags object

resource communicationService 'Microsoft.Communication/communicationServices@2023-04-01' existing = {
  name: communicationServiceNome
}

resource cofre 'Microsoft.KeyVault/vaults@2023-07-01' = {
  name: nome
  location: location
  tags: tags
  properties: {
    tenantId: subscription().tenantId
    sku: {
      family: 'A'
      name: 'standard'
    }
    enableRbacAuthorization: true
    enableSoftDelete: true
    softDeleteRetentionInDays: 7
    publicNetworkAccess: 'Enabled'
  }
}

resource segredoEmail 'Microsoft.KeyVault/vaults/secrets@2023-07-01' = {
  parent: cofre
  name: 'email-connection-string'
  properties: {
    value: communicationService.listKeys().primaryConnectionString
  }
}

output nome string = cofre.name
output segredoEmailUri string = segredoEmail.properties.secretUri