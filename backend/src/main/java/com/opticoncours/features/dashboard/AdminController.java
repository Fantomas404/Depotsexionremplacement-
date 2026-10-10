package com.opticoncours.features.dashboard;

import com.opticoncours.common.response.ApiResponse;
import com.opticoncours.features.auth.AuthRecords;
import com.opticoncours.features.auth.Role;
import com.opticoncours.features.auth.UserRepository;
import com.opticoncours.features.candidatures.CandidatureRecords;
import com.opticoncours.features.candidatures.CandidatureService;
import com.opticoncours.features.candidatures.StatutCandidature;
import com.opticoncours.features.concours.ConcoursRecords;
import com.opticoncours.features.concours.ConcoursService;
import com.opticoncours.features.deliberation.DeliberationRecords;
import com.opticoncours.features.deliberation.DeliberationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Endpoints réservés aux administrateurs pour la gestion globale de la plateforme")
public class AdminController {

    private final DashboardService dashboardService;
    private final ConcoursService concoursService;
    private final CandidatureService candidatureService;
    private final UserRepository userRepository;
    private final DeliberationService deliberationService;

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Obtenir les statistiques globales pour le tableau de bord administrateur")
    public ResponseEntity<ApiResponse<DashboardRecords.DashboardStats>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getStats()));
    }

    @GetMapping("/users")
    @Operation(summary = "Liste de tous les utilisateurs (candidats et administrateurs)")
    public ResponseEntity<ApiResponse<List<AuthRecords.UserResponse>>> getAllUsers() {
        List<AuthRecords.UserResponse> users = userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(AuthRecords.UserResponse::fromUser)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @GetMapping("/candidatures")
    @Operation(summary = "Liste de toutes les candidatures, avec filtrage optionnel")
    public ResponseEntity<ApiResponse<List<CandidatureRecords.CandidatureResponse>>> getAllCandidatures(
            @RequestParam(required = false) UUID concoursId,
            @RequestParam(required = false) StatutCandidature statut
    ) {
        return ResponseEntity.ok(ApiResponse.ok(candidatureService.getAllCandidatures(concoursId, statut)));
    }

    @PatchMapping("/candidatures/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'un dossier de candidature (Validation/Rejet)")
    public ResponseEntity<ApiResponse<CandidatureRecords.CandidatureResponse>> updateCandidatureStatut(
            @PathVariable UUID id,
            @Valid @RequestBody CandidatureRecords.StatutCandidatureRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour avec succès", candidatureService.updateStatut(id, req)));
    }

    @GetMapping("/concours")
    @Operation(summary = "Liste de tous les concours (y compris brouillons et clôturés)")
    public ResponseEntity<ApiResponse<List<ConcoursRecords.ConcoursResponse>>> getAllConcours() {
        return ResponseEntity.ok(ApiResponse.ok(concoursService.getAllConcours()));
    }

    @PostMapping("/concours")
    @Operation(summary = "Créer une nouvelle session de concours")
    public ResponseEntity<ApiResponse<ConcoursRecords.ConcoursResponse>> createConcours(
            @Valid @RequestBody ConcoursRecords.ConcoursRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Concours créé avec succès", concoursService.createConcours(req)));
    }

    @PutMapping("/concours/{id}")
    @Operation(summary = "Mettre à jour une session de concours existante")
    public ResponseEntity<ApiResponse<ConcoursRecords.ConcoursResponse>> updateConcours(
            @PathVariable UUID id,
            @Valid @RequestBody ConcoursRecords.ConcoursRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Concours mis à jour avec succès", concoursService.updateConcours(id, req)));
    }

    @DeleteMapping("/concours/{id}")
    @Operation(summary = "Supprimer définitivement un concours")
    public ResponseEntity<ApiResponse<Void>> deleteConcours(@PathVariable UUID id) {
        concoursService.deleteConcours(id);
        return ResponseEntity.ok(ApiResponse.ok("Concours supprimé avec succès", null));
    }

    @GetMapping("/concours/{concoursId}/eligible-notes")
    @Operation(summary = "Liste des candidats éligibles à la saisie de notes (Dossiers complets)")
    public ResponseEntity<ApiResponse<List<CandidatureRecords.CandidatureResponse>>> getEligibleCandidatsForNotes(
            @PathVariable UUID concoursId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(deliberationService.getEligibleCandidatsForNotes(concoursId)));
    }

    @PostMapping("/concours/{concoursId}/notes/batch")
    @Operation(summary = "Saisie en lot des notes pour plusieurs candidats")
    public ResponseEntity<ApiResponse<Void>> enregistrerNotesBatch(
            @PathVariable UUID concoursId,
            @Valid @RequestBody List<DeliberationRecords.NotesBatchRequest> req
    ) {
        deliberationService.enregistrerNotesBatch(concoursId, req);
        return ResponseEntity.ok(ApiResponse.ok("Notes enregistrées avec succès pour le lot de candidats.", null));
    }

    @PostMapping("/concours/{concoursId}/deliberation")
    @Operation(summary = "Calculer les classements et délibérer pour une session de concours")
    public ResponseEntity<ApiResponse<DeliberationRecords.DeliberationSummaryResponse>> calculerDeliberation(
            @PathVariable UUID concoursId,
            @RequestBody(required = false) DeliberationRecords.DeliberationConfigRequest config
    ) {
        DeliberationRecords.DeliberationSummaryResponse summary = deliberationService.calculerDeliberation(concoursId, config);
        return ResponseEntity.ok(ApiResponse.ok("Délibération effectuée avec succès.", summary));
    }

    @PostMapping("/concours/{concoursId}/publier-resultats")
    @Operation(summary = "Publier officiellement les résultats d'un concours")
    public ResponseEntity<ApiResponse<Void>> publierResultats(@PathVariable UUID concoursId) {
        deliberationService.publierResultats(concoursId);
        return ResponseEntity.ok(ApiResponse.ok("Les résultats ont été publiés et sont désormais accessibles au public.", null));
    }
}
