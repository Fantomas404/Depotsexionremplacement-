package com.opticoncours.features.concours;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "t_concours_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Concours {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "titre_session", nullable = false)
    private String titre;

    @Column(name = "description_detaillee", columnDefinition = "TEXT")
    private String description;

    @Column(name = "criteres_eligibilite", columnDefinition = "TEXT")
    private String conditions;

    @NotNull
    @Column(name = "date_ouverture_depot", nullable = false)
    private LocalDate dateDebut;

    @NotNull
    @Column(name = "date_cloture_depot", nullable = false)
    private LocalDate dateFin;

    @Column(name = "date_deliberation_prevue")
    private LocalDate dateDeliberation;

    @Min(1)
    @Column(name = "quota_places_ouvertes", nullable = false)
    @Builder.Default
    private Integer placesDisponibles = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_avancement", nullable = false)
    @Builder.Default
    private StatutConcours statut = StatutConcours.BROUILLON;

    @OneToMany(mappedBy = "concours", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnoreProperties("concours")
    private List<Epreuve> epreuves = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "date_creation_systeme", updatable = false)
    private LocalDateTime createdAt;

    public void addEpreuve(Epreuve epreuve) {
        epreuves.add(epreuve);
        epreuve.setConcours(this);
    }

    public void removeEpreuve(Epreuve epreuve) {
        epreuves.remove(epreuve);
        epreuve.setConcours(null);
    }
}
