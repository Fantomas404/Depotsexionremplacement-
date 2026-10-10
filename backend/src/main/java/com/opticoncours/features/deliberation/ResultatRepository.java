package com.opticoncours.features.deliberation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultatRepository extends JpaRepository<Resultat, UUID> {
    Optional<Resultat> findByCandidatureId(UUID candidatureId);

    @Query("SELECT r FROM Resultat r WHERE r.candidature.concours.id = :concoursId ORDER BY r.rang ASC")
    List<Resultat> findByConcoursIdOrderByRangAsc(@Param("concoursId") UUID concoursId);

    @Query("SELECT r FROM Resultat r WHERE r.candidature.concours.id = :concoursId AND r.estPublie = true ORDER BY r.rang ASC")
    List<Resultat> findPublicResultsByConcoursId(@Param("concoursId") UUID concoursId);

    long countByEstPublieTrue();
}
