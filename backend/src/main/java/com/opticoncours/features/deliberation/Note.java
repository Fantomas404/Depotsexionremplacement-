package com.opticoncours.features.deliberation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opticoncours.features.candidatures.Candidature;
import com.opticoncours.features.concours.Epreuve;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "t_evaluations_notes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dossier_ref_id", "epreuve_ref_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "note_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_ref_id", nullable = false)
    @JsonIgnoreProperties({"notes", "resultat"})
    private Candidature candidature;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "epreuve_ref_id", nullable = false)
    private Epreuve epreuve;

    @DecimalMin("0.0")
    @DecimalMax("20.0")
    @Column(name = "note_obtenue", nullable = false)
    private Double valeurNote;
}
