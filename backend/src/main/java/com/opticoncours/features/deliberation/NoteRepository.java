package com.opticoncours.features.deliberation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findByCandidatureId(UUID candidatureId);
    Optional<Note> findByCandidatureIdAndEpreuveId(UUID candidatureId, UUID epreuveId);
}
