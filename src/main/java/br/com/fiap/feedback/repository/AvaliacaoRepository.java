package br.com.fiap.feedback.repository;

import br.com.fiap.feedback.model.dto.relatorio.AvaliacoesPorDiaDTO;
import br.com.fiap.feedback.model.dto.relatorio.AvaliacoesPorUrgenciaDTO;
import br.com.fiap.feedback.model.entity.Avaliacao;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AvaliacaoRepository implements PanacheRepositoryBase<Avaliacao, UUID> {

    private static final Sort MAIS_ANTIGAS_PRIMEIRO = Sort.by("dataEnvio");

    public List<Avaliacao> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return list("dataEnvio >= ?1 and dataEnvio < ?2", MAIS_ANTIGAS_PRIMEIRO, inicio, fim);
    }

    public Double calcularMediaNotas(LocalDateTime inicio, LocalDateTime fim) {
        return getEntityManager()
                .createQuery("""
                        select avg(a.nota)
                        from Avaliacao a
                        where a.dataEnvio >= :inicio and a.dataEnvio < :fim
                        """, Double.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();
    }

    public List<AvaliacoesPorDiaDTO> contarPorDia(LocalDateTime inicio, LocalDateTime fim) {
        return getEntityManager()
                .createQuery("""
                        select new br.com.fiap.feedback.model.dto.relatorio.AvaliacoesPorDiaDTO(cast(a.dataEnvio as LocalDate), count(a))
                        from Avaliacao a
                        where a.dataEnvio >= :inicio and a.dataEnvio < :fim
                        group by cast(a.dataEnvio as LocalDate)
                        order by cast(a.dataEnvio as LocalDate)
                        """, AvaliacoesPorDiaDTO.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public List<AvaliacoesPorUrgenciaDTO> contarPorUrgencia(LocalDateTime inicio, LocalDateTime fim) {
        return getEntityManager()
                .createQuery("""
                        select new br.com.fiap.feedback.model.dto.relatorio.AvaliacoesPorUrgenciaDTO(a.urgencia, count(a))
                        from Avaliacao a
                        where a.dataEnvio >= :inicio and a.dataEnvio < :fim
                        group by a.urgencia
                        """, AvaliacoesPorUrgenciaDTO.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }
}