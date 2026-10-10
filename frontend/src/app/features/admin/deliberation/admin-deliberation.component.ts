import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { Concours, DeliberationConfig, DeliberationSummary } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-admin-deliberation',
  standalone: true,
  imports: [CommonModule, FormsModule, StatusBadgeComponent],
  templateUrl: './admin-deliberation.component.html',
  styleUrls: ['./admin-deliberation.component.css']
})
export class AdminDeliberationComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  concoursList: Concours[] = [];
  selectedConcoursId: number | null = null;
  selectedConcours: Concours | null = null;

  config: DeliberationConfig = {
    seuilAdmission: 12.0,
    seuilListeAttente: 10.0,
    placesMaxAdmis: 10
  };

  summary: DeliberationSummary | null = null;

  loadingConcours: boolean = true;
  loadingSummary: boolean = false;
  calculating: boolean = false;
  publishing: boolean = false;

  successMessage: string | null = null;
  errorMessage: string | null = null;

  ngOnInit(): void {
    this.loadConcoursList();
  }

  loadConcoursList(): void {
    this.loadingConcours = true;
    this.cdr.detectChanges();
    this.adminService.getAllConcours().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.concoursList = res.data;
          if (this.concoursList.length > 0) {
            this.selectedConcoursId = this.concoursList[0].id;
            this.onConcoursChange();
          }
        }
        this.loadingConcours = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loadingConcours = false;
        this.cdr.detectChanges();
      }
    });
  }

  onConcoursChange(): void {
    if (!this.selectedConcoursId) return;

    this.selectedConcours = this.concoursList.find(c => c.id === +this.selectedConcoursId!) || null;
    if (this.selectedConcours) {
      this.config.placesMaxAdmis = this.selectedConcours.placesDisponibles;
    }
    this.loadSummary();
  }

  loadSummary(): void {
    if (!this.selectedConcoursId) return;

    this.loadingSummary = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    this.adminService.getDeliberation(this.selectedConcoursId).subscribe({
      next: (res) => {
        if (res.success && res.data && res.data.classements && res.data.classements.length > 0) {
          this.summary = res.data;
        } else {
          this.summary = null;
        }
        this.loadingSummary = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.summary = null;
        this.loadingSummary = false;
        this.cdr.detectChanges();
      }
    });
  }

  calculerDeliberation(): void {
    if (!this.selectedConcoursId) return;

    this.calculating = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    this.adminService.calculerDeliberation(this.selectedConcoursId, this.config).subscribe({
      next: (res) => {
        this.calculating = false;
        if (res.success && res.data) {
          this.summary = res.data;
          this.successMessage = "Délibération et calcul des moyennes pondérées effectués avec succès !";
          setTimeout(() => this.successMessage = null, 4000);
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.calculating = false;
        this.errorMessage = err.error?.message || "Erreur lors du calcul de la délibération. Vérifiez que des notes ont été saisies.";
        this.cdr.detectChanges();
      }
    });
  }

  publierResultats(): void {
    if (!this.selectedConcoursId) return;

    if (!confirm("Voulez-vous officiellement publier les résultats ? Ils deviendront immédiatement visibles à tous les candidats.")) {
      return;
    }

    this.publishing = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    this.adminService.publierResultats(this.selectedConcoursId).subscribe({
      next: (res) => {
        this.publishing = false;
        if (res.success && res.data) {
          this.summary = res.data;
          if (this.selectedConcours) {
            this.selectedConcours.statut = 'PUBLIE';
          }
          this.successMessage = "🎉 Les résultats sont désormais officiellement publiés et en ligne !";
          setTimeout(() => this.successMessage = null, 5000);
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.publishing = false;
        this.errorMessage = err.error?.message || "Erreur lors de la publication des résultats.";
        this.cdr.detectChanges();
      }
    });
  }
}
