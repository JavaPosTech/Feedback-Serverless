param functionAppNome string
param functionAppPrincipalId string
param storageNome string
param cofreNome string
param leitoresObjectIds array
param implantadorPrincipalId string

var papeis = {
  leitor: 'acdd72a7-3385-48ef-bd42-f606fba81ae7'
  colaboradorSite: 'de139f84-1756-47ae-9be6-808fbbe84772'
  usuarioSegredos: '4633458b-17de-408a-b874-0445c86b69e6'
  donoDadosBlob: 'b7e6dc6d-f1e8-4753-8033-0f276bb0955b'
  colaboradorDadosFila: '974c5e8b-45b9-4653-ba55-5f855dd0fb88'
}

resource functionApp 'Microsoft.Web/sites@2024-04-01' existing = {
  name: functionAppNome
}

resource conta 'Microsoft.Storage/storageAccounts@2023-05-01' existing = {
  name: storageNome
}

resource cofre 'Microsoft.KeyVault/vaults@2023-07-01' existing = {
  name: cofreNome
}

resource funcaoBlob 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  scope: conta
  name: guid(conta.id, functionAppPrincipalId, papeis.donoDadosBlob)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', papeis.donoDadosBlob)
    principalId: functionAppPrincipalId
    principalType: 'ServicePrincipal'
  }
}

resource funcaoFila 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  scope: conta
  name: guid(conta.id, functionAppPrincipalId, papeis.colaboradorDadosFila)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', papeis.colaboradorDadosFila)
    principalId: functionAppPrincipalId
    principalType: 'ServicePrincipal'
  }
}

resource funcaoSegredos 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  scope: cofre
  name: guid(cofre.id, functionAppPrincipalId, papeis.usuarioSegredos)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', papeis.usuarioSegredos)
    principalId: functionAppPrincipalId
    principalType: 'ServicePrincipal'
  }
}

resource implantador 'Microsoft.Authorization/roleAssignments@2022-04-01' = if (!empty(implantadorPrincipalId)) {
  scope: functionApp
  name: guid(functionApp.id, implantadorPrincipalId, papeis.colaboradorSite)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', papeis.colaboradorSite)
    principalId: implantadorPrincipalId
    principalType: 'ServicePrincipal'
  }
}

resource leitores 'Microsoft.Authorization/roleAssignments@2022-04-01' = [for leitor in leitoresObjectIds: {
  name: guid(resourceGroup().id, leitor, papeis.leitor)
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', papeis.leitor)
    principalId: leitor
  }
}]