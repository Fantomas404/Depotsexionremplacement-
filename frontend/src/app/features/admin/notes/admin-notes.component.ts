import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { Candidature, Concours, Epreuve, NotesBatchRequest } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

interface CandidateGradeRow {
  candidatureId: number;
  nom: string;
  prenom: string;
  email: string;
  notesMap: { [epreuveId: number]: number };
}

@Component({
  selector: 'app-admin-notes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-notes.component.html',
  styleUrls: ['./admin-notes.component.css']
})
export class AdminNotesComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  concoursList: Concours[] = [];
  selectedConcoursId: number | null = null;
  selectedConcours: Concours | null = null;

  epreuves: Epreuve[] = [];
  rows: CandidateGradeRow[] = [];

  loadingConcours: boolean = true;
  loadingMatrix: boolean = false;
  saving: boolean = false;
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
    this.epreuves = this.selectedConcours?.epreuves || [];
    this.loadMatrix();
  }

  loadMatrix(): void {
    if (!this.selectedConcoursId) return;

    this.loadingMatrix = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    this.adminService.getMatrixSaisieNotes(this.selectedConcoursId).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.buildMatrixRows(res.data);
        }
        this.loadingMatrix = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Erreur lors du chargement de la grille de saisie.";
        this.loadingMatrix = false;
        this.cdr.detectChanges();
      }
    });
  }

  private buildMatrixRows(candidatures: Candidature[]): void {
    this.rows = candidatures.map(cand => {
      const notesMap: { [epreuveId: number]: number } = {};

      if (cand.notes) {
        cand.notes.forEach(n => {
          notesMap[n.epreuveId] = n.valeurNote;
        });
      }

      this.epreuves.forEach(e => {
        if (e.id && notesMap[e.id] === undefined) {
          notesMap[e.id] = 0;
        }
      });

      return {
        candidatureId: cand.id,
        nom: cand.candidatNom,
        prenom: cand.candidatPrenom,
        email: cand.candidatEmail,
        notesMap
      };
    });
  }

  calculateAverage(row: CandidateGradeRow): number {
    if (!this.epreuves || this.epreuves.length === 0) return 0;
    const totalCoef = this.epreuves.reduce((acc, e) => acc + e.coefficient, 0);
    if (totalCoef <= 0) return 0;

    let points = 0;
    this.epreuves.forEach(e => {
      if (e.id) {
        const note = row.notesMap[e.id] || 0;
        points += (note * e.coefficient);
      }
    });

    return Math.round((points / totalCoef) * 100) / 100;
  }

  saveAllNotes(): void {
    if (!this.selectedConcoursId) return;

    this.saving = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    const batchRequests: NotesBatchRequest[] = this.rows.map(row => ({
      candidatureId: row.candidatureId,
      notes: this.epreuves.map(e => ({
        epreuveId: e.id!,
        valeurNote: Number(row.notesMap[e.id!] || 0)
      }))
    }));

    this.adminService.enregistrerNotes(this.selectedConcoursId, batchRequests).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = "Toutes les notes ont été enregistrées avec succès !";
        this.cdr.detectChanges();
        setTimeout(() => {
          this.successMessage = null;
          this.cdr.detectChanges();
        }, 4000);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || "Erreur lors de l'enregistrement des notes.";
        this.cdr.detectChanges();
      }
    });
  }
}
