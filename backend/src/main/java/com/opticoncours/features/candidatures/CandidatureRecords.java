package com.opticoncours.features.candidatures;

import com.opticoncours.features.deliberation.DeliberationRecords;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CandidatureRecords {

    public record StatutCandidatureRequest(
            @NotNull(message = "Le nouveau statut est requis")
            StatutCandidature statut,

            String motifRejet
    ) {}

    public record CandidatureResponse(
            UUID id,
            UUID candidatId,
            String candidatNom,
            String candidatPrenom,
            String candidatEmail,
            String candidatTelephone,
            UUID concoursId,
            String concoursTitre,
            String cvUrl,
            String diplomeUrl,
            String photoUrl,
            StatutCandidature statut,
            String motifRejet,
            LocalDateTime dateSoumission,
            List<DeliberationRecords.NoteRecord> notes,
            DeliberationRecords.ResultatResponse resultat
    ) {
        public static CandidatureResponse fromEntity(Candidature c) {
            if (c == null) return null;

            var cand = c.getCandidat();
            var concours = c.getConcours();

            List<DeliberationRecords.NoteRecord> noteRecords = Collections.emptyList();
            if (c.getNotes() != null) {
                noteRecords = c.getNotes().stream().map(DeliberationRecords.NoteRecord::fromEntity).toList();
            }

            DeliberationRecords.ResultatResponse res = null;
            if (c.getResultat() != null) {
                res = DeliberationRecords.ResultatResponse.fromEntity(c.getResultat());
            }

            return new CandidatureResponse(
                    c.getId(),
                    cand != null ? cand.getId() : null,
                    cand != null ? cand.getNom() : "",
                    cand != null ? cand.getPrenom() : "",
                    cand != null ? cand.getEmail() : "",
                    cand != null ? cand.getTelephone() : null,
                    concours != null ? concours.getId() : null,
                    concours != null ? concours.getTitre() : "",
                    c.getCvUrl(),
                    c.getDiplomeUrl(),
                    c.getPhotoUrl(),
                    c.getStatut(),
                    c.getMotifRejet(),
                    c.getDateSoumission(),
                    noteRecords,
                    res
            );
        }
    }
}
