import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { Concours, ConcoursRequest, Epreuve, StatutConcours } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-admin-concours',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-concours.component.html',
  styleUrls: ['./admin-concours.component.css']
})
export class AdminConcoursComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  concoursList: Concours[] = [];
  loading: boolean = true;
  saving: boolean = false;
  showModal: boolean = false;
  editMode: boolean = false;
  currentId: number | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  form: ConcoursRequest = {
    titre: '',
    description: '',
    conditions: '',
    dateDebut: '',
    dateFin: '',
    dateDeliberation: '',
    placesDisponibles: 10,
    statut: 'OUVERT',
    epreuves: []
  };

  ngOnInit(): void {
    this.loadConcours();
  }

  loadConcours(): void {
    this.loading = true;
    this.cdr.detectChanges();
    this.adminService.getAllConcours().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.concoursList = res.data;
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

  openCreateModal(): void {
    this.editMode = false;
    this.currentId = null;
    this.errorMessage = null;
    this.form = {
      titre: '',
      description: '',
      conditions: '',
      dateDebut: new Date().toISOString().split('T')[0],
      dateFin: new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0],
      dateDeliberation: new Date(Date.now() + 45 * 86400000).toISOString().split('T')[0],
      placesDisponibles: 10,
      statut: 'OUVERT',
      epreuves: [
        { intitule: 'Culture Générale', coefficient: 2, dureeMinutes: 180 },
        { intitule: 'Épreuve Spécialité', coefficient: 3, dureeMinutes: 240 }
      ]
    };
    this.showModal = true;
    this.cdr.detectChanges();
  }

  openEditModal(c: Concours): void {
    this.editMode = true;
    this.currentId = c.id;
    this.errorMessage = null;
    this.form = {
      titre: c.titre,
      description: c.description || '',
      conditions: c.conditions || '',
      dateDebut: c.dateDebut,
      dateFin: c.dateFin,
      dateDeliberation: c.dateDeliberation || '',
      placesDisponibles: c.placesDisponibles,
      statut: c.statut,
      epreuves: c.epreuves ? c.epreuves.map(e => ({ ...e })) : []
    };
    this.showModal = true;
    this.cdr.detectChanges();
  }

  closeModal(): void {
    this.showModal = false;
    this.cdr.detectChanges();
  }

  addEpreuve(): void {
    this.form.epreuves.push({
      intitule: '',
      coefficient: 1,
      dureeMinutes: 120
    });
  }

  removeEpreuve(index: number): void {
    this.form.epreuves.splice(index, 1);
  }

  saveConcours(): void {
    if (!this.form.titre || !this.form.dateDebut || !this.form.dateFin) {
      this.errorMessage = "Veuillez renseigner le titre et les dates du concours.";
      return;
    }

    if (this.form.epreuves.length === 0) {
      this.errorMessage = "Le concours doit comporter au moins une épreuve.";
      return;
    }

    this.saving = true;
    this.errorMessage = null;

    const action = this.editMode && this.currentId ?
      this.adminService.updateConcours(this.currentId, this.form) :
      this.adminService.createConcours(this.form);

    action.subscribe({
      next: () => {
        this.saving = false;
        this.closeModal();
        this.successMessage = this.editMode ? "Concours mis à jour avec succès." : "Nouveau concours créé avec succès.";
        this.loadConcours();
        setTimeout(() => this.successMessage = null, 4000);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || "Erreur lors de l'enregistrement du concours.";
      }
    });
  }

  updateStatut(c: Concours, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const newStatut = select.value as StatutConcours;

    this.adminService.updateConcoursStatut(c.id, newStatut).subscribe({
      next: () => {
        c.statut = newStatut;
        this.successMessage = `Statut de "${c.titre}" mis à jour (${newStatut}).`;
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Erreur lors de la mise à jour du statut.";
      }
    });
  }

  deleteConcours(c: Concours): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer définitivement le concours "${c.titre}" ?`)) {
      this.adminService.deleteConcours(c.id).subscribe({
        next: () => {
          this.loadConcours();
          this.successMessage = "Concours supprimé avec succès.";
          setTimeout(() => this.successMessage = null, 3000);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || "Impossible de supprimer ce concours.";
        }
      });
    }
  }
}
