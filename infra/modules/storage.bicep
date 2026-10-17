param location string
param nome string
param tags object

resource conta 'Microsoft.Storage/storageAccounts@2023-05-01' = {
  name: nome
  location: location
  tags: tags
  kind: 'StorageV2'
  sku: {
    name: 'Standard_LRS'
  }
  properties: {
    minimumTlsVersion: 'TLS1_2'
    supportsHttpsTrafficOnly: true
    allowBlobPublicAccess: false
    allowSharedKeyAccess: false
    defaultToOAuthAuthentication: true
  }
}

resource blobs 'Microsoft.Storage/storageAccounts/blobServices@2023-05-01' = {
  parent: conta
  name: 'default'
}

resource pacotes 'Microsoft.Storage/storageAccounts/blobServices/containers@2023-05-01' = {
  parent: blobs
  name: 'deployments'
  properties: {
    publicAccess: 'None'
  }
}

resource filas 'Microsoft.Storage/storageAccounts/queueServices@2023-05-01' = {
  parent: conta
  name: 'default'
}

resource filaCriticas 'Microsoft.Storage/storageAccounts/queueServices/queues@2023-05-01' = {
  parent: filas
  name: 'avaliacoes-criticas'
}

resource filaPoison 'Microsoft.Storage/storageAccounts/queueServices/queues@2023-05-01' = {
  parent: filas
  name: 'avaliacoes-criticas-poison'
}

output nome string = conta.name
output blobEndpoint string = conta.properties.primaryEndpoints.blob
output queueEndpoint string = conta.properties.primaryEndpoints.queue