package br.com.fiap.feedback.template;

import io.quarkus.qute.TemplateExtension;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@TemplateExtension
public class FormatacaoExtensions {

    static String formatar(BigDecimal valor) {
        var formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));

        formato.setMinimumFractionDigits(1);
        formato.setMaximumFractionDigits(1);

        return formato.format(valor);
    }
}