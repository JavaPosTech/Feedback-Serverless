using 'main.bicep'

param ambiente = 'dev'
param emailsAdministradores = [
  'administrador@exemplo.com'
]
param adminSqlLogin = 'seu-usuario@seu-tenant.onmicrosoft.com'
param adminSqlObjectId = '00000000-0000-0000-0000-000000000000'
param adminSqlTipo = 'User'
param leitoresObjectIds = []
param implantadorPrincipalId = ''
param orcamentoMensal = 20