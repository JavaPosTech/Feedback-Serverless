param location string
param nome string
param adminLogin string
param adminObjectId string
param adminTipo string
param tags object

resource servidor 'Microsoft.Sql/servers@2023-08-01' = {
  name: nome
  location: location
  tags: tags
  properties: {
    minimalTlsVersion: '1.2'
    publicNetworkAccess: 'Enabled'
    administrators: {
      administratorType: 'ActiveDirectory'
      azureADOnlyAuthentication: true
      login: adminLogin
      sid: adminObjectId
      tenantId: subscription().tenantId
      principalType: adminTipo
    }
  }
}

resource acessoServicosAzure 'Microsoft.Sql/servers/firewallRules@2023-08-01' = {
  parent: servidor
  name: 'AllowAllWindowsAzureIps'
  properties: {
    startIpAddress: '0.0.0.0'
    endIpAddress: '0.0.0.0'
  }
}

resource banco 'Microsoft.Sql/servers/databases@2023-08-01' = {
  parent: servidor
  name: 'feedback'
  location: location
  tags: tags
  sku: {
    name: 'GP_S_Gen5'
    tier: 'GeneralPurpose'
    family: 'Gen5'
    capacity: 2
  }
  properties: {
    autoPauseDelay: 60
    minCapacity: json('0.5')
    maxSizeBytes: 34359738368
    useFreeLimit: true
    freeLimitExhaustionBehavior: 'AutoPause'
  }
}

output fqdn string = servidor.properties.fullyQualifiedDomainName
output databaseNome string = banco.name