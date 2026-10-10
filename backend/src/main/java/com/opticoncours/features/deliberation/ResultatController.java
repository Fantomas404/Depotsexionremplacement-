package com.opticoncours.features.deliberation;

import com.opticoncours.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resultats")
@RequiredArgsConstructor
@Tag(name = "Résultats Publics", description = "Consultation ouverte des classements et résultats de délibération publiés")
public class ResultatController {

    private final DeliberationService deliberationService;

    @GetMapping("/concours/{concoursId}")
    @Operation(summary = "Classement officiel publié pour une session de concours donnée")
    public ResponseEntity<ApiResponse<List<DeliberationRecords.ResultatResponse>>> getResultatsByConcours(
            @PathVariable UUID concoursId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(deliberationService.getResultatsPublicsByConcours(concoursId)));
    }

    @GetMapping("/concours/{concoursId}/summary")
    @Operation(summary = "Synthèse statistique des délibérations pour une session de concours")
    public ResponseEntity<ApiResponse<DeliberationRecords.DeliberationSummaryResponse>> getSummary(
            @PathVariable UUID concoursId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(deliberationService.getSummaryByConcours(concoursId)));
    }

    @GetMapping("/candidature/{candidatureId}")
    @Operation(summary = "Résultat individuel d'un candidat pour une candidature donnée")
    public ResponseEntity<ApiResponse<DeliberationRecords.ResultatResponse>> getResultatCandidat(
            @PathVariable UUID candidatureId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(deliberationService.getResultatCandidat(candidatureId)));
    }
}
