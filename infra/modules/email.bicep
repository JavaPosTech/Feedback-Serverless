param nome string
param tags object

resource servicoEmail 'Microsoft.Communication/emailServices@2023-04-01' = {
  name: 'ecs-${nome}'
  location: 'global'
  tags: tags
  properties: {
    dataLocation: 'Brazil'
  }
}

resource dominio 'Microsoft.Communication/emailServices/domains@2023-04-01' = {
  parent: servicoEmail
  name: 'AzureManagedDomain'
  location: 'global'
  tags: tags
  properties: {
    domainManagement: 'AzureManaged'
    userEngagementTracking: 'Disabled'
  }
}

resource communicationService 'Microsoft.Communication/communicationServices@2023-04-01' = {
  name: 'acs-${nome}'
  location: 'global'
  tags: tags
  properties: {
    dataLocation: 'Brazil'
    linkedDomains: [
      dominio.id
    ]
  }
}

output communicationServiceNome string = communicationService.name
output remetente string = 'DoNotReply@${dominio.properties.mailFromSenderDomain}'