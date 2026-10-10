package com.opticoncours.features.candidatures;

import com.opticoncours.common.exception.BadRequestException;
import com.opticoncours.common.exception.ConflictException;
import com.opticoncours.common.exception.ResourceNotFoundException;
import com.opticoncours.common.exception.UnauthorizedException;
import com.opticoncours.features.auth.AuthService;
import com.opticoncours.features.auth.Role;
import com.opticoncours.features.auth.User;
import com.opticoncours.features.concours.Concours;
import com.opticoncours.features.concours.ConcoursRepository;
import com.opticoncours.features.concours.StatutConcours;
import com.opticoncours.features.documents.DocumentStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final ConcoursRepository concoursRepository;
    private final AuthService authService;
    private final DocumentStorageService storageService;

    @Transactional
    public CandidatureRecords.CandidatureResponse postuler(
            UUID concoursId,
            MultipartFile cvFile,
            MultipartFile diplomeFile,
            MultipartFile photoFile
    ) {
        User currentUser = authService.getAuthenticatedUser();

        Concours concours = concoursRepository.findById(concoursId)
                .orElseThrow(() -> new ResourceNotFoundException("Concours introuvable : " + concoursId));

        if (concours.getStatut() != StatutConcours.OUVERT) {
            throw new BadRequestException("Les candidatures pour cette session sont actuellement fermées.");
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(concours.getDateDebut()) || today.isAfter(concours.getDateFin())) {
            throw new BadRequestException("La période de dépôt des candidatures n'est pas active pour ce concours.");
        }

        if (candidatureRepository.existsByCandidatIdAndConcoursId(currentUser.getId(), concoursId)) {
            throw new ConflictException("Vous avez déjà enregistré un dossier de candidature pour cette session.");
        }

        String cvUrl = storageService.storeFile(cvFile, "cv_" + currentUser.getNom().toLowerCase());
        String diplomeUrl = storageService.storeFile(diplomeFile, "diplome_" + currentUser.getNom().toLowerCase());
        String photoUrl = storageService.storeFile(photoFile, "photo_" + currentUser.getNom().toLowerCase());

        Candidature candidature = Candidature.builder()
                .candidat(currentUser)
                .concours(concours)
                .cvUrl(cvUrl)
                .diplomeUrl(diplomeUrl)
                .photoUrl(photoUrl)
                .statut(StatutCandidature.EN_ATTENTE)
                .build();

        Candidature saved = candidatureRepository.save(candidature);
        return CandidatureRecords.CandidatureResponse.fromEntity(saved);
    }

    public List<CandidatureRecords.CandidatureResponse> getMyCandidatures() {
        User currentUser = authService.getAuthenticatedUser();
        List<Candidature> list = candidatureRepository.findByCandidatIdOrderByDateSoumissionDesc(currentUser.getId());
        return list.stream().map(CandidatureRecords.CandidatureResponse::fromEntity).toList();
    }

    public CandidatureRecords.CandidatureResponse getCandidatureById(UUID id) {
        User currentUser = authService.getAuthenticatedUser();
        Candidature c = candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier de candidature introuvable : " + id));

        if (currentUser.getRole() != Role.ROLE_ADMIN && !c.getCandidat().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à consulter ce dossier.");
        }

        return CandidatureRecords.CandidatureResponse.fromEntity(c);
    }

    @Transactional
    public CandidatureRecords.CandidatureResponse updateStatut(UUID id, CandidatureRecords.StatutCandidatureRequest req) {
        Candidature c = candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier de candidature introuvable : " + id));

        c.setStatut(req.statut());
        if (req.statut() == StatutCandidature.REJETE) {
            c.setMotifRejet(req.motifRejet());
        } else {
            c.setMotifRejet(null);
        }

        Candidature updated = candidatureRepository.save(c);
        return CandidatureRecords.CandidatureResponse.fromEntity(updated);
    }

    public List<CandidatureRecords.CandidatureResponse> getAllCandidatures(UUID concoursId, StatutCandidature statut) {
        List<Candidature> list;
        if (concoursId != null && statut != null) {
            list = candidatureRepository.findByConcoursIdAndStatut(concoursId, statut);
        } else if (concoursId != null) {
            list = candidatureRepository.findByConcoursIdOrderByDateSoumissionDesc(concoursId);
        } else {
            list = candidatureRepository.findAllByOrderByDateSoumissionDesc();
        }
        return list.stream().map(CandidatureRecords.CandidatureResponse::fromEntity).toList();
    }
}
