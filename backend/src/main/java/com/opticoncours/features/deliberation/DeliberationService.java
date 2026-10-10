package com.opticoncours.features.deliberation;

import com.opticoncours.common.exception.BadRequestException;
import com.opticoncours.common.exception.ResourceNotFoundException;
import com.opticoncours.features.candidatures.Candidature;
import com.opticoncours.features.candidatures.CandidatureRecords;
import com.opticoncours.features.candidatures.CandidatureRepository;
import com.opticoncours.features.candidatures.StatutCandidature;
import com.opticoncours.features.concours.Concours;
import com.opticoncours.features.concours.ConcoursRepository;
import com.opticoncours.features.concours.Epreuve;
import com.opticoncours.features.concours.StatutConcours;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliberationService {

    private final ConcoursRepository concoursRepository;
    private final CandidatureRepository candidatureRepository;
    private final NoteRepository noteRepository;
    private final ResultatRepository resultatRepository;

    public List<CandidatureRecords.CandidatureResponse> getEligibleCandidatsForNotes(UUID concoursId) {
        List<Candidature> list = candidatureRepository.findByConcoursIdOrderByDateSoumissionDesc(concoursId);
        return list.stream()
                .filter(c -> c.getStatut() == StatutCandidature.DOSSIER_COMPLET ||
                             c.getStatut() == StatutCandidature.EN_DELIBERATION ||
                             c.getStatut() == StatutCandidature.DELIBERE)
                .map(CandidatureRecords.CandidatureResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void enregistrerNotesBatch(UUID concoursId, List<DeliberationRecords.NotesBatchRequest> batchRequests) {
        Concours concours = concoursRepository.findById(concoursId)
                .orElseThrow(() -> new ResourceNotFoundException("Concours introuvable : " + concoursId));

        Map<UUID, Epreuve> epreuvesMap = new HashMap<>();
        for (Epreuve ep : concours.getEpreuves()) {
            epreuvesMap.put(ep.getId(), ep);
        }

        for (DeliberationRecords.NotesBatchRequest batch : batchRequests) {
            Candidature candidature = candidatureRepository.findById(batch.candidatureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable : " + batch.candidatureId()));

            if (!candidature.getConcours().getId().equals(concoursId)) {
                throw new BadRequestException("Cette candidature n'est pas inscrite à cette session de concours.");
            }

            for (DeliberationRecords.NoteSaisieItemDTO item : batch.notes()) {
                Epreuve epreuve = epreuvesMap.get(item.epreuveId());
                if (epreuve == null) {
                    throw new BadRequestException("Épreuve non reconnue pour ce concours : " + item.epreuveId());
                }

                if (item.valeurNote() < 0.0 || item.valeurNote() > 20.0) {
                    throw new BadRequestException("La note doit être comprise entre 0.0 et 20.0.");
                }

                Note note = noteRepository.findByCandidatureIdAndEpreuveId(candidature.getId(), epreuve.getId())
                        .orElse(Note.builder()
                                .candidature(candidature)
                                .epreuve(epreuve)
                                .build());

                note.setValeurNote(item.valeurNote());
                noteRepository.save(note);
            }

            candidature.setStatut(StatutCandidature.EN_DELIBERATION);
            candidatureRepository.save(candidature);
        }

        concours.setStatut(StatutConcours.EN_DELIBERATION);
        concoursRepository.save(concours);
    }

    @Transactional
    public DeliberationRecords.DeliberationSummaryResponse calculerDeliberation(
            UUID concoursId,
            DeliberationRecords.DeliberationConfigRequest config
    ) {
        Concours concours = concoursRepository.findById(concoursId)
                .orElseThrow(() -> new ResourceNotFoundException("Concours introuvable : " + concoursId));

        List<Epreuve> epreuves = concours.getEpreuves();
        if (epreuves == null || epreuves.isEmpty()) {
            throw new BadRequestException("Aucune épreuve configurée pour cette session.");
        }

        double totalCoeff = 0.0;
        for (Epreuve ep : epreuves) {
            totalCoeff += ep.getCoefficient();
        }

        if (totalCoeff <= 0) {
            throw new BadRequestException("Somme des coefficients invalide.");
        }

        List<Candidature> candidatures = candidatureRepository.findByConcoursIdOrderByDateSoumissionDesc(concoursId)
                .stream()
                .filter(c -> c.getStatut() == StatutCandidature.DOSSIER_COMPLET ||
                             c.getStatut() == StatutCandidature.EN_DELIBERATION ||
                             c.getStatut() == StatutCandidature.DELIBERE)
                .toList();

        if (candidatures.isEmpty()) {
            throw new BadRequestException("Aucun candidat admissible aux délibérations n'a été trouvé.");
        }

        record CandMoyenne(Candidature cand, double moyenne) {}
        List<CandMoyenne> listMoyennes = new ArrayList<>();

        for (Candidature c : candidatures) {
            List<Note> notes = noteRepository.findByCandidatureId(c.getId());
            double totalPoints = 0.0;

            for (Note n : notes) {
                double coeff = n.getEpreuve().getCoefficient();
                totalPoints += n.getValeurNote() * coeff;
            }

            double moyenne = round2(totalPoints / totalCoeff);
            listMoyennes.add(new CandMoyenne(c, moyenne));
        }

        listMoyennes.sort((a, b) -> Double.compare(b.moyenne(), a.moyenne()));

        double seuilAdmission = (config != null && config.seuilAdmission() != null) ? config.seuilAdmission() : 10.0;
        double seuilAttente = (config != null && config.seuilListeAttente() != null) ? config.seuilListeAttente() : 8.0;
        int placesDisponibles = (config != null && config.placesMaxAdmis() != null && config.placesMaxAdmis() > 0)
                ? config.placesMaxAdmis()
                : concours.getPlacesDisponibles();

        List<Resultat> resultatsGeneres = new ArrayList<>();
        int rang = 1;
        int admisCount = 0;
        int attenteCount = 0;
        int refuseCount = 0;

        for (CandMoyenne item : listMoyennes) {
            Mention mention;
            if (item.moyenne() >= seuilAdmission && admisCount < placesDisponibles) {
                mention = Mention.ADMIS;
                admisCount++;
            } else if (item.moyenne() >= seuilAttente) {
                mention = Mention.LISTE_D_ATTENTE;
                attenteCount++;
            } else {
                mention = Mention.NON_ADMIS;
                refuseCount++;
            }

            Resultat res = resultatRepository.findByCandidatureId(item.cand().getId())
                    .orElse(Resultat.builder().candidature(item.cand()).build());

            res.setMoyenneGenerale(item.moyenne());
            res.setRang(rang++);
            res.setMention(mention);
            res.setEstPublie(false);

            resultatsGeneres.add(resultatRepository.save(res));

            item.cand().setStatut(StatutCandidature.DELIBERE);
            candidatureRepository.save(item.cand());
        }

        double meilleure = listMoyennes.isEmpty() ? 0.0 : listMoyennes.get(0).moyenne();
        double pire = listMoyennes.isEmpty() ? 0.0 : listMoyennes.get(listMoyennes.size() - 1).moyenne();
        double sommeMoyennes = listMoyennes.stream().mapToDouble(CandMoyenne::moyenne).sum();
        double moyennePromo = listMoyennes.isEmpty() ? 0.0 : round2(sommeMoyennes / listMoyennes.size());

        List<DeliberationRecords.ResultatResponse> classementResponses = resultatsGeneres.stream()
                .map(DeliberationRecords.ResultatResponse::fromEntity)
                .toList();

        return new DeliberationRecords.DeliberationSummaryResponse(
                concours.getId(),
                concours.getTitre(),
                listMoyennes.size(),
                admisCount,
                attenteCount,
                refuseCount,
                meilleure,
                pire,
                moyennePromo,
                false,
                classementResponses
        );
    }

    @Transactional
    public void publierResultats(UUID concoursId) {
        Concours concours = concoursRepository.findById(concoursId)
                .orElseThrow(() -> new ResourceNotFoundException("Concours introuvable : " + concoursId));

        List<Resultat> resultats = resultatRepository.findByConcoursIdOrderByRangAsc(concoursId);
        if (resultats.isEmpty()) {
            throw new BadRequestException("Aucune délibération n'a encore été effectuée pour cette session.");
        }

        LocalDateTime now = LocalDateTime.now();
        for (Resultat r : resultats) {
            r.setEstPublie(true);
            r.setDatePublication(now);
            resultatRepository.save(r);
        }

        concours.setStatut(StatutConcours.PUBLIE);
        concoursRepository.save(concours);
    }

    public List<DeliberationRecords.ResultatResponse> getResultatsPublicsByConcours(UUID concoursId) {
        List<Resultat> list = resultatRepository.findPublicResultsByConcoursId(concoursId);
        return list.stream().map(DeliberationRecords.ResultatResponse::fromEntity).toList();
    }

    public DeliberationRecords.ResultatResponse getResultatCandidat(UUID candidatureId) {
        Resultat res = resultatRepository.findByCandidatureId(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Résultat non disponible pour cette candidature."));
        return DeliberationRecords.ResultatResponse.fromEntity(res);
    }

    public DeliberationRecords.DeliberationSummaryResponse getSummaryByConcours(UUID concoursId) {
        Concours concours = concoursRepository.findById(concoursId)
                .orElseThrow(() -> new ResourceNotFoundException("Concours introuvable : " + concoursId));

        List<Resultat> resultats = resultatRepository.findByConcoursIdOrderByRangAsc(concoursId);
        int total = resultats.size();
        int admis = 0;
        int attente = 0;
        int refuse = 0;

        double sum = 0.0;
        for (Resultat r : resultats) {
            sum += r.getMoyenneGenerale();
            switch (r.getMention()) {
                case ADMIS -> admis++;
                case LISTE_D_ATTENTE -> attente++;
                case NON_ADMIS -> refuse++;
            }
        }

        double meilleure = resultats.isEmpty() ? 0.0 : resultats.get(0).getMoyenneGenerale();
        double pire = resultats.isEmpty() ? 0.0 : resultats.get(resultats.size() - 1).getMoyenneGenerale();
        double moyennePromo = total > 0 ? round2(sum / total) : 0.0;
        boolean estPublie = !resultats.isEmpty() && resultats.get(0).getEstPublie();

        return new DeliberationRecords.DeliberationSummaryResponse(
                concours.getId(),
                concours.getTitre(),
                total,
                admis,
                attente,
                refuse,
                meilleure,
                pire,
                moyennePromo,
                estPublie,
                resultats.stream().map(DeliberationRecords.ResultatResponse::fromEntity).toList()
        );
    }

    private double round2(double val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
