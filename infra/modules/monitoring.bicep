param location string
param nome string
param emails array
param tags object

resource workspace 'Microsoft.OperationalInsights/workspaces@2023-09-01' = {
  name: 'log-${nome}'
  location: location
  tags: tags
  properties: {
    sku: {
      name: 'PerGB2018'
    }
    retentionInDays: 30
    workspaceCapping: {
      dailyQuotaGb: 1
    }
  }
}

resource appInsights 'Microsoft.Insights/components@2020-02-02' = {
  name: 'appi-${nome}'
  location: location
  tags: tags
  kind: 'web'
  properties: {
    Application_Type: 'web'
    WorkspaceResourceId: workspace.id
  }
}

resource grupoAcao 'Microsoft.Insights/actionGroups@2023-01-01' = {
  name: 'ag-${nome}'
  location: 'global'
  tags: tags
  properties: {
    groupShortName: 'feedback'
    enabled: true
    emailReceivers: [for (email, indice) in emails: {
      name: 'administrador-${indice}'
      emailAddress: email
      useCommonAlertSchema: true
    }]
  }
}

resource alertaFalhas 'Microsoft.Insights/scheduledQueryRules@2023-12-01' = {
  name: 'alerta-falhas-${nome}'
  location: location
  tags: tags
  properties: {
    displayName: 'Execuções de função com falha'
    description: 'Dispara quando alguma das funções termina com erro nos últimos 5 minutos.'
    severity: 1
    enabled: true
    evaluationFrequency: 'PT5M'
    windowSize: 'PT5M'
    scopes: [
      appInsights.id
    ]
    criteria: {
      allOf: [
        {
          query: 'requests | where success == false'
          timeAggregation: 'Count'
          operator: 'GreaterThan'
          threshold: 0
          failingPeriods: {
            numberOfEvaluationPeriods: 1
            minFailingPeriodsToAlert: 1
          }
        }
      ]
    }
    actions: {
      actionGroups: [
        grupoAcao.id
      ]
    }
  }
}

resource alertaExcecoes 'Microsoft.Insights/scheduledQueryRules@2023-12-01' = {
  name: 'alerta-excecoes-${nome}'
  location: location
  tags: tags
  properties: {
    displayName: 'Exceções na aplicação'
    description: 'Dispara quando a aplicação registra mais de 5 exceções em 15 minutos.'
    severity: 2
    enabled: true
    evaluationFrequency: 'PT15M'
    windowSize: 'PT15M'
    scopes: [
      appInsights.id
    ]
    criteria: {
      allOf: [
        {
          query: 'exceptions'
          timeAggregation: 'Count'
          operator: 'GreaterThan'
          threshold: 5
          failingPeriods: {
            numberOfEvaluationPeriods: 1
            minFailingPeriodsToAlert: 1
          }
        }
      ]
    }
    actions: {
      actionGroups: [
        grupoAcao.id
      ]
    }
  }
}

output appInsightsConnectionString string = appInsights.properties.ConnectionString