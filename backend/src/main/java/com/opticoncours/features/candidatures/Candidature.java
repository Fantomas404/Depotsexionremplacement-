package com.opticoncours.features.candidatures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opticoncours.features.auth.User;
import com.opticoncours.features.concours.Concours;
import com.opticoncours.features.deliberation.Note;
import com.opticoncours.features.deliberation.Resultat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_candidatures_dossiers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"candidat_ref_id", "session_concours_ref_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "dossier_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "candidat_ref_id", nullable = false)
    private User candidat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "session_concours_ref_id", nullable = false)
    @JsonIgnoreProperties({"candidatures", "epreuves"})
    private Concours concours;

    @Column(name = "chemin_fichier_cv")
    private String cvUrl;

    @Column(name = "chemin_fichier_diplome")
    private String diplomeUrl;

    @Column(name = "chemin_fichier_photo")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_instruction_dossier", nullable = false)
    @Builder.Default
    private StatutCandidature statut = StatutCandidature.EN_ATTENTE;

    @Column(name = "justification_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnoreProperties("candidature")
    private List<Note> notes = new ArrayList<>();

    @OneToOne(mappedBy = "candidature", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("candidature")
    private Resultat resultat;

    @CreationTimestamp
    @Column(name = "date_soumission_dossier", updatable = false)
    private LocalDateTime dateSoumission;
}
