package br.com.fiap.feedback.function;

import br.com.fiap.feedback.service.RelatorioService;
import com.microsoft.azure.functions.annotation.ExponentialBackoffRetry;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class GerarRelatorioFunction {

    private final RelatorioService relatorioService;

    @FunctionName("GerarRelatorio")
    @ExponentialBackoffRetry(maxRetryCount = 3, minimumInterval = "00:00:30", maximumInterval = "00:05:00")
    public void gerarRelatorio(
            @TimerTrigger(
                    name = "agendamento",
                    schedule = "0 0 11 * * 1")
            String agendamento) {
        relatorioService.gerarRelatorioSemanal();
    }
}