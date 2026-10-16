package br.com.fiap.feedback.template;

import br.com.fiap.feedback.model.dto.notificacao.AvaliacaoCriticaDTO;
import br.com.fiap.feedback.model.dto.relatorio.RelatorioSemanalDTO;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "email")
public class EmailTemplates {

    public static native TemplateInstance urgencia(AvaliacaoCriticaDTO avaliacao);

    public static native TemplateInstance relatorioSemanal(RelatorioSemanalDTO relatorio);
}