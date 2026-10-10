package com.opticoncours.features.concours;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConcoursRepository extends JpaRepository<Concours, UUID> {
    List<Concours> findByStatut(StatutConcours statut);
    List<Concours> findAllByOrderByCreatedAtDesc();
}
