package com.opticoncours.features.deliberation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opticoncours.features.candidatures.Candidature;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "t_deliberation_resultats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resultat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "resultat_id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_ref_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"resultat", "notes"})
    private Candidature candidature;

    @Column(name = "moyenne_generale_ponderee", nullable = false)
    private Double moyenneGenerale;

    @Column(name = "classement_merite_rang", nullable = false)
    private Integer rang;

    @Enumerated(EnumType.STRING)
    @Column(name = "mention_deliberation", nullable = false)
    private Mention mention;

    @Column(name = "statut_visibilite_publique", nullable = false)
    @Builder.Default
    private Boolean estPublie = false;

    @Column(name = "date_publication_officielle")
    private LocalDateTime datePublication;
}
