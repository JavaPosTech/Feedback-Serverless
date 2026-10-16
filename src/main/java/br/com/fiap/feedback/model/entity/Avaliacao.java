package br.com.fiap.feedback.model.entity;

import br.com.fiap.feedback.enums.Urgencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false)
    private Integer nota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Urgencia urgencia;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;
}