package com.opticoncours.features.concours;

import com.opticoncours.common.exception.BadRequestException;
import com.opticoncours.common.exception.ResourceNotFoundException;
import com.opticoncours.features.candidatures.CandidatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConcoursService {

    private final ConcoursRepository concoursRepository;
    private final EpreuveRepository epreuveRepository;
    private final CandidatureRepository candidatureRepository;

    public List<ConcoursRecords.ConcoursResponse> getPublicConcours() {
        List<Concours> openConcours = concoursRepository.findAllByOrderByCreatedAtDesc();
        List<ConcoursRecords.ConcoursResponse> result = new ArrayList<>(openConcours.size());

        for (Concours c : openConcours) {
            long count = candidatureRepository.countByConcoursId(c.getId());
            result.add(ConcoursRecords.ConcoursResponse.fromEntity(c, count));
        }

        return result;
    }

    public List<ConcoursRecords.ConcoursResponse> getAllConcours() {
        List<Concours> list = concoursRepository.findAllByOrderByCreatedAtDesc();
        List<ConcoursRecords.ConcoursResponse> result = new ArrayList<>(list.size());

        for (Concours c : list) {
            long count = candidatureRepository.countByConcoursId(c.getId());
            result.add(ConcoursRecords.ConcoursResponse.fromEntity(c, count));
        }

        return result;
    }

    public ConcoursRecords.ConcoursResponse getConcoursById(UUID id) {
        Concours c = findConcoursOrThrow(id);
        long count = candidatureRepository.countByConcoursId(id);
        return ConcoursRecords.ConcoursResponse.fromEntity(c, count);
    }

    public Concours findConcoursOrThrow(UUID id) {
        return concoursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session de concours introuvable avec l'identifiant : " + id));
    }

    @Transactional
    public ConcoursRecords.ConcoursResponse createConcours(ConcoursRecords.ConcoursRequest req) {
        validateDates(req.dateDebut(), req.dateFin(), req.dateDeliberation());

        Concours concours = Concours.builder()
                .titre(req.titre().trim())
                .description(req.description())
                .conditions(req.conditions())
                .dateDebut(req.dateDebut())
                .dateFin(req.dateFin())
                .dateDeliberation(req.dateDeliberation())
                .placesDisponibles(req.placesDisponibles() > 0 ? req.placesDisponibles() : 1)
                .statut(req.statut() != null ? req.statut() : StatutConcours.OUVERT)
                .build();

        if (req.epreuves() != null && !req.epreuves().isEmpty()) {
            for (ConcoursRecords.EpreuveRecord epReq : req.epreuves()) {
                Epreuve epreuve = Epreuve.builder()
                        .intitule(epReq.intitule().trim())
                        .coefficient(epReq.coefficient() > 0 ? epReq.coefficient() : 1.0)
                        .dureeMinutes(epReq.dureeMinutes() > 0 ? epReq.dureeMinutes() : 60)
                        .build();
                concours.addEpreuve(epreuve);
            }
        }

        Concours saved = concoursRepository.save(concours);
        return ConcoursRecords.ConcoursResponse.fromEntity(saved, 0L);
    }

    @Transactional
    public ConcoursRecords.ConcoursResponse updateConcours(UUID id, ConcoursRecords.ConcoursRequest req) {
        Concours concours = findConcoursOrThrow(id);
        validateDates(req.dateDebut(), req.dateFin(), req.dateDeliberation());

        concours.setTitre(req.titre().trim());
        concours.setDescription(req.description());
        concours.setConditions(req.conditions());
        concours.setDateDebut(req.dateDebut());
        concours.setDateFin(req.dateFin());
        concours.setDateDeliberation(req.dateDeliberation());
        concours.setPlacesDisponibles(req.placesDisponibles() > 0 ? req.placesDisponibles() : 1);

        if (req.statut() != null) {
            concours.setStatut(req.statut());
        }

        if (req.epreuves() != null) {
            concours.getEpreuves().clear();
            for (ConcoursRecords.EpreuveRecord epReq : req.epreuves()) {
                Epreuve epreuve = Epreuve.builder()
                        .intitule(epReq.intitule().trim())
                        .coefficient(epReq.coefficient() > 0 ? epReq.coefficient() : 1.0)
                        .dureeMinutes(epReq.dureeMinutes() > 0 ? epReq.dureeMinutes() : 60)
                        .build();
                concours.addEpreuve(epreuve);
            }
        }

        Concours updated = concoursRepository.save(concours);
        long count = candidatureRepository.countByConcoursId(id);
        return ConcoursRecords.ConcoursResponse.fromEntity(updated, count);
    }

    @Transactional
    public void deleteConcours(UUID id) {
        Concours c = findConcoursOrThrow(id);
        long count = candidatureRepository.countByConcoursId(id);
        if (count > 0) {
            throw new BadRequestException("Impossible de supprimer ce concours : " + count + " candidature(s) y sont rattachée(s).");
        }
        concoursRepository.delete(c);
    }

    private void validateDates(LocalDate debut, LocalDate fin, LocalDate delib) {
        if (fin.isBefore(debut)) {
            throw new BadRequestException("La date de clôture des candidatures doit être postérieure à la date d'ouverture.");
        }
        if (delib != null && delib.isBefore(fin)) {
            throw new BadRequestException("La date prévisionnelle de délibération doit être postérieure à la date de clôture.");
        }
    }
}
