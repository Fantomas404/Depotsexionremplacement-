package com.opticoncours.features.candidatures;

import com.opticoncours.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
@Tag(name = "Espace Candidatures", description = "Soumission et suivi d'avancement des dossiers candidats")
public class CandidatureController {

    private final CandidatureService candidatureService;

    @PostMapping(value = "/postuler", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Dépôt d'un nouveau dossier de candidature avec pièces justificatives")
    public ResponseEntity<ApiResponse<CandidatureRecords.CandidatureResponse>> postuler(
            @RequestParam("concoursId") UUID concoursId,
            @RequestParam("cv") MultipartFile cv,
            @RequestParam("diplome") MultipartFile diplome,
            @RequestParam("photo") MultipartFile photo
    ) {
        CandidatureRecords.CandidatureResponse response = candidatureService.postuler(concoursId, cv, diplome, photo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Votre candidature a été déposée avec succès.", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Liste des candidatures enregistrées par le candidat connecté")
    public ResponseEntity<ApiResponse<List<CandidatureRecords.CandidatureResponse>>> getMyCandidatures() {
        return ResponseEntity.ok(ApiResponse.ok(candidatureService.getMyCandidatures()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultation détaillée d'un dossier de candidature")
    public ResponseEntity<ApiResponse<CandidatureRecords.CandidatureResponse>> getCandidatureById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(candidatureService.getCandidatureById(id)));
    }
}
