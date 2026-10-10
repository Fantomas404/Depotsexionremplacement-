package com.opticoncours.features.dashboard;

import com.opticoncours.features.candidatures.CandidatureRecords;
import com.opticoncours.features.concours.ConcoursRecords;

import java.util.List;

public class DashboardRecords {

    public record DashboardStats(
            long totalConcours,
            long concoursOuverts,
            long totalCandidatsInscrits,
            long totalCandidatures,
            long candidaturesEnAttente,
            long candidaturesValidees,
            long candidaturesRejetees,
            long resultatsPublies,
            List<ConcoursRecords.ConcoursResponse> derniersConcours,
            List<CandidatureRecords.CandidatureResponse> dernieresCandidatures
    ) {}
}
