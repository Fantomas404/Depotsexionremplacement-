package com.opticoncours.features.concours;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ConcoursRecords {

    public record EpreuveRecord(
            UUID id,
            @NotBlank(message = "L'intitulé de l'épreuve est requis")
            String intitule,

            @Min(value = 1, message = "Le coefficient doit être d'au moins 1")
            double coefficient,

            @Min(value = 15, message = "La durée minimale est de 15 minutes")
            int dureeMinutes
    ) {
        public static EpreuveRecord fromEntity(Epreuve e) {
            if (e == null) return null;
            return new EpreuveRecord(e.getId(), e.getIntitule(), e.getCoefficient(), e.getDureeMinutes());
        }
    }

    public record ConcoursRequest(
            @NotBlank(message = "Le titre du concours est obligatoire")
            String titre,

            String description,
            String conditions,

            @NotNull(message = "La date de début est obligatoire")
            LocalDate dateDebut,

            @NotNull(message = "La date de fin est obligatoire")
            LocalDate dateFin,

            LocalDate dateDeliberation,

            @Min(value = 1, message = "Le nombre de places doit être supérieur ou égal à 1")
            int placesDisponibles,

            StatutConcours statut,

            List<EpreuveRecord> epreuves
    ) {}

    public record ConcoursResponse(
            UUID id,
            String titre,
            String description,
            String conditions,
            LocalDate dateDebut,
            LocalDate dateFin,
            LocalDate dateDeliberation,
            int placesDisponibles,
            StatutConcours statut,
            LocalDateTime createdAt,
            long nombreCandidats,
            double totalCoefficients,
            List<EpreuveRecord> epreuves
    ) {
        public static ConcoursResponse fromEntity(Concours c, long countCandidats) {
            if (c == null) return null;

            List<EpreuveRecord> eps = c.getEpreuves() != null
                    ? c.getEpreuves().stream().map(EpreuveRecord::fromEntity).toList()
                    : Collections.emptyList();

            double totalCoeff = 0.0;
            for (EpreuveRecord e : eps) {
                totalCoeff += e.coefficient();
            }

            return new ConcoursResponse(
                    c.getId(),
                    c.getTitre(),
                    c.getDescription(),
                    c.getConditions(),
                    c.getDateDebut(),
                    c.getDateFin(),
                    c.getDateDeliberation(),
                    c.getPlacesDisponibles(),
                    c.getStatut(),
                    c.getCreatedAt(),
                    countCandidats,
                    totalCoeff,
                    eps
            );
        }
    }
}
