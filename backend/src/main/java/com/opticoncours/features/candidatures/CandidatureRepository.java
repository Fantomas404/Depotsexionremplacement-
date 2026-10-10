package com.opticoncours.features.candidatures;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, UUID> {
    List<Candidature> findByCandidatIdOrderByDateSoumissionDesc(UUID candidatId);
    List<Candidature> findByConcoursIdOrderByDateSoumissionDesc(UUID concoursId);
    List<Candidature> findByConcoursIdAndStatut(UUID concoursId, StatutCandidature statut);
    boolean existsByCandidatIdAndConcoursId(UUID candidatId, UUID concoursId);
    Optional<Candidature> findByCandidatIdAndConcoursId(UUID candidatId, UUID concoursId);
    long countByConcoursId(UUID concoursId);
    long countByStatut(StatutCandidature statut);
    List<Candidature> findAllByOrderByDateSoumissionDesc();
}
