import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { Candidature, Concours, StatutCandidature } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-admin-candidatures',
  standalone: true,
  imports: [CommonModule, FormsModule, StatusBadgeComponent],
  templateUrl: './admin-candidatures.component.html',
  styleUrls: ['./admin-candidatures.component.css']
})
export class AdminCandidaturesComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  concoursList: Concours[] = [];
  candidatures: Candidature[] = [];

  selectedConcoursId: number | '' = '';
  selectedStatut: StatutCandidature | '' = '';

  loading: boolean = true;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  showRejectModal: boolean = false;
  selectedCandidature: Candidature | null = null;
  rejectReason: string = 'Dossier incomplet ou diplôme non conforme.';

  ngOnInit(): void {
    this.loadConcoursList();
    this.loadCandidatures();
  }

  loadConcoursList(): void {
    this.adminService.getAllConcours().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.concoursList = res.data;
          this.cdr.detectChanges();
        }
      }
    });
  }

  loadCandidatures(): void {
    this.loading = true;
    this.cdr.detectChanges();
    const cid = this.selectedConcoursId ? +this.selectedConcoursId : undefined;
    const st = this.selectedStatut ? this.selectedStatut as StatutCandidature : undefined;

    this.adminService.getCandidatures(cid, st).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.candidatures = res.data;
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  validerDossier(cand: Candidature): void {
    this.adminService.updateCandidatureStatut(cand.id, 'DOSSIER_COMPLET').subscribe({
      next: (res) => {
        cand.statut = 'DOSSIER_COMPLET';
        cand.motifRejet = undefined;
        this.successMessage = `Dossier #${cand.id} de ${cand.candidatNom} validé avec succès !`;
        setTimeout(() => this.successMessage = null, 3500);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Erreur lors de la validation du dossier.";
      }
    });
  }

  openRejectModal(cand: Candidature): void {
    this.selectedCandidature = cand;
    this.rejectReason = 'Dossier incomplet ou critères d\'âge/diplôme non satisfaits.';
    this.showRejectModal = true;
    this.cdr.detectChanges();
  }

  confirmReject(): void {
    if (!this.selectedCandidature) return;

    this.adminService.updateCandidatureStatut(this.selectedCandidature.id, 'REJETE', this.rejectReason).subscribe({
      next: () => {
        if (this.selectedCandidature) {
          this.selectedCandidature.statut = 'REJETE';
          this.selectedCandidature.motifRejet = this.rejectReason;
        }
        this.showRejectModal = false;
        this.successMessage = `Dossier #${this.selectedCandidature?.id} rejeté.`;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.successMessage = null;
          this.cdr.detectChanges();
        }, 3500);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Erreur lors du rejet du dossier.";
        this.cdr.detectChanges();
      }
    });
  }
}
