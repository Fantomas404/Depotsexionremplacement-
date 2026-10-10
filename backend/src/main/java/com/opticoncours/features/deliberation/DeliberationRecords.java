package com.opticoncours.features.deliberation;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DeliberationRecords {

    public record NoteRecord(
            UUID id,
            UUID epreuveId,
            String epreuveIntitule,
            double coefficient,
            double valeurNote,
            double notePonderee
    ) {
        public static NoteRecord fromEntity(Note n) {
            if (n == null) return null;
            double coeff = n.getEpreuve() != null ? n.getEpreuve().getCoefficient() : 1.0;
            double val = n.getValeurNote() != null ? n.getValeurNote() : 0.0;
            return new NoteRecord(
                    n.getId(),
                    n.getEpreuve() != null ? n.getEpreuve().getId() : null,
                    n.getEpreuve() != null ? n.getEpreuve().getIntitule() : "",
                    coeff,
                    val,
                    Math.round(val * coeff * 100.0) / 100.0
            );
        }
    }

    public record ResultatResponse(
            UUID id,
            UUID candidatureId,
            UUID candidatId,
            String candidatNom,
            String candidatPrenom,
            String candidatEmail,
            String candidatTelephone,
            UUID concoursId,
            String concoursTitre,
            double moyenneGenerale,
            int rang,
            Mention mention,
            boolean estPublie,
            LocalDateTime datePublication,
            List<NoteRecord> notes
    ) {
        public static ResultatResponse fromEntity(Resultat r) {
            if (r == null) return null;
            var cand = r.getCandidature();
            var user = cand != null ? cand.getCandidat() : null;
            var concours = cand != null ? cand.getConcours() : null;

            List<NoteRecord> notesList = Collections.emptyList();
            if (cand != null && cand.getNotes() != null) {
                notesList = cand.getNotes().stream().map(NoteRecord::fromEntity).toList();
            }

            return new ResultatResponse(
                    r.getId(),
                    cand != null ? cand.getId() : null,
                    user != null ? user.getId() : null,
                    user != null ? user.getNom() : "",
                    user != null ? user.getPrenom() : "",
                    user != null ? user.getEmail() : "",
                    user != null ? user.getTelephone() : null,
                    concours != null ? concours.getId() : null,
                    concours != null ? concours.getTitre() : "",
                    r.getMoyenneGenerale(),
                    r.getRang(),
                    r.getMention(),
                    r.getEstPublie(),
                    r.getDatePublication(),
                    notesList
            );
        }
    }

    public record DeliberationSummaryResponse(
            UUID concoursId,
            String concoursTitre,
            int totalCandidatsEvalues,
            int totalAdmis,
            int totalListeAttente,
            int totalRefuses,
            double meilleureMoyenne,
            double plusFaibleMoyenne,
            double moyenneGeneralePromotion,
            boolean estPublie,
            List<ResultatResponse> classements
    ) {}

    public record DeliberationConfigRequest(
            Double seuilAdmission,
            Double seuilListeAttente,
            Integer placesMaxAdmis
    ) {}

    public record NoteSaisieItemDTO(
            @NotNull(message = "L'identifiant de l'épreuve est requis")
            UUID epreuveId,

            @DecimalMin(value = "0.0", message = "La note minimale est 0.0")
            @DecimalMax(value = "20.0", message = "La note maximale est 20.0")
            double valeurNote
    ) {}

    public record NotesBatchRequest(
            @NotNull(message = "L'identifiant de la candidature est requis")
            UUID candidatureId,

            List<NoteSaisieItemDTO> notes
    ) {}
}
