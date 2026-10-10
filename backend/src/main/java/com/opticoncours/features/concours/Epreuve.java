package com.opticoncours.features.concours;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "t_epreuves_concours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Epreuve {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "epreuve_id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "intitule_epreuve", nullable = false)
    private String intitule;

    @DecimalMin("0.5")
    @Column(name = "coefficient_ponderation", nullable = false)
    private Double coefficient;

    @Min(15)
    @Column(name = "duree_epreuve_minutes", nullable = false)
    private Integer dureeMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concours_ref_id", nullable = false)
    @JsonIgnore
    private Concours concours;
}
