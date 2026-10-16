package br.com.fiap.feedback.model.mapper;

import br.com.fiap.feedback.model.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.fiap.feedback.model.dto.avaliacao.SalvarAvaliacaoDTO;
import br.com.fiap.feedback.model.dto.notificacao.AvaliacaoCriticaDTO;
import br.com.fiap.feedback.model.entity.Avaliacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface AvaliacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "urgencia", ignore = true)
    @Mapping(target = "dataEnvio", ignore = true)
    Avaliacao toAvaliacao(SalvarAvaliacaoDTO salvarAvaliacaoDTO);

    AvaliacaoResponseDTO toAvaliacaoResponseDTO(Avaliacao avaliacao);

    AvaliacaoCriticaDTO toAvaliacaoCriticaDTO(Avaliacao avaliacao);

    List<AvaliacaoResponseDTO> toAvaliacaoResponseDTOList(List<Avaliacao> avaliacoes);
}