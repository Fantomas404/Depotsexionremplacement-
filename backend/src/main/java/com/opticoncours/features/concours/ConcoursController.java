package com.opticoncours.features.concours;

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
@RequestMapping("/api/concours")
@RequiredArgsConstructor
@Tag(name = "Catalogue Public des Concours", description = "Consultation libre des sessions de concours et de leurs épreuves")
public class ConcoursController {

    private final ConcoursService concoursService;

    @GetMapping
    @Operation(summary = "Liste des concours ouverts ou publiés à destination du grand public")
    public ResponseEntity<ApiResponse<List<ConcoursRecords.ConcoursResponse>>> getPublicConcours() {
        return ResponseEntity.ok(ApiResponse.ok(concoursService.getPublicConcours()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultation détaillée d'un concours par son identifiant unique UUID")
    public ResponseEntity<ApiResponse<ConcoursRecords.ConcoursResponse>> getConcoursById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(concoursService.getConcoursById(id)));
    }
}
