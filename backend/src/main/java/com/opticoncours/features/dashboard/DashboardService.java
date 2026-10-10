package com.opticoncours.features.dashboard;

import com.opticoncours.features.auth.Role;
import com.opticoncours.features.auth.UserRepository;
import com.opticoncours.features.candidatures.CandidatureRepository;
import com.opticoncours.features.candidatures.CandidatureRecords;
import com.opticoncours.features.candidatures.StatutCandidature;
import com.opticoncours.features.concours.ConcoursRepository;
import com.opticoncours.features.concours.StatutConcours;
import com.opticoncours.features.concours.ConcoursRecords;
import com.opticoncours.features.deliberation.ResultatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ConcoursRepository concoursRepository;
    private final CandidatureRepository candidatureRepository;
    private final UserRepository userRepository;
    private final ResultatRepository resultatRepository;

    public DashboardRecords.DashboardStats getStats() {
        long totalConcours = concoursRepository.count();
        long concoursOuverts = concoursRepository.findByStatut(StatutConcours.OUVERT).size();
        long totalCandidatsInscrits = userRepository.findByRole(Role.ROLE_CANDIDAT).size();
        long totalCandidatures = candidatureRepository.count();
        long candidaturesEnAttente = candidatureRepository.countByStatut(StatutCandidature.EN_ATTENTE);
        long candidaturesValidees = candidatureRepository.countByStatut(StatutCandidature.DOSSIER_COMPLET);
        long candidaturesRejetees = candidatureRepository.countByStatut(StatutCandidature.REJETE);
        long resultatsPublies = resultatRepository.countByEstPublieTrue();

        var derniersConcours = concoursRepository.findAllByOrderByCreatedAtDesc().stream()
                .limit(5)
                .map(c -> ConcoursRecords.ConcoursResponse.fromEntity(c, candidatureRepository.countByConcoursId(c.getId())))
                .toList();

        var dernieresCandidatures = candidatureRepository.findAllByOrderByDateSoumissionDesc().stream()
                .limit(10)
                .map(CandidatureRecords.CandidatureResponse::fromEntity)
                .toList();

        return new DashboardRecords.DashboardStats(
                totalConcours,
                concoursOuverts,
                totalCandidatsInscrits,
                totalCandidatures,
                candidaturesEnAttente,
                candidaturesValidees,
                candidaturesRejetees,
                resultatsPublies,
                derniersConcours,
                dernieresCandidatures
        );
    }
}
