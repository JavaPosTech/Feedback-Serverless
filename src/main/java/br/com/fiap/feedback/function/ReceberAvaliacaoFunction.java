package br.com.fiap.feedback.function;

import br.com.fiap.feedback.exceptions.handler.FunctionExceptionHandler;
import br.com.fiap.feedback.model.dto.avaliacao.SalvarAvaliacaoDTO;
import br.com.fiap.feedback.service.AvaliacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class ReceberAvaliacaoFunction {

    private final ObjectMapper objectMapper;
    private final AvaliacaoService avaliacaoService;
    private final FunctionExceptionHandler functionExceptionHandler;

    @FunctionName("ReceberAvaliacao")
    public HttpResponseMessage salvarAvaliacao(
            @HttpTrigger(
                    name = "requisicao",
                    methods = HttpMethod.POST,
                    route = "avaliacao",
                    authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<Optional<String>> requisicao) {
        try {
            var salvarAvaliacaoDTO = objectMapper.readValue(requisicao.getBody().orElse(""), SalvarAvaliacaoDTO.class);
            var avaliacao = avaliacaoService.salvar(salvarAvaliacaoDTO);

            return requisicao.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(objectMapper.writeValueAsString(avaliacao))
                    .build();
        } catch (Exception ex) {
            return functionExceptionHandler.handle(ex, requisicao);
        }
    }
}