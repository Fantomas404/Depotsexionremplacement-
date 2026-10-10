import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ConcoursService } from '../../../core/services/concours.service';
import { Concours } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, StatusBadgeComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  private concoursService = inject(ConcoursService);
  private cdr = inject(ChangeDetectorRef);

  concoursList: Concours[] = [];
  filteredConcours: Concours[] = [];
  searchTerm: string = '';
  loading: boolean = true;

  stats = {
    ouverts: 0,
    totalPlaces: 0,
    totalCandidats: 0
  };

  ngOnInit(): void {
    this.loadConcours();
  }

  loadConcours(): void {
    this.loading = true;
    this.cdr.detectChanges();
    console.log('[HomeComponent] Chargement des concours depuis API...');
    this.concoursService.getPublicConcours().subscribe({
      next: (res) => {
        console.log('[HomeComponent] Données reçues :', res);
        if (res && res.success && res.data) {
          this.concoursList = res.data;
          this.filteredConcours = res.data;
          this.calculateStats();
        } else {
          this.concoursList = [];
          this.filteredConcours = [];
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('[HomeComponent] Erreur lors de la récupération des concours :', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterConcours(): void {
    if (!this.searchTerm.trim()) {
      this.filteredConcours = this.concoursList;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredConcours = this.concoursList.filter(c =>
        c.titre.toLowerCase().includes(term) ||
        (c.description && c.description.toLowerCase().includes(term))
      );
    }
  }

  private calculateStats(): void {
    this.stats.ouverts = this.concoursList.filter(c => c.statut === 'OUVERT').length;
    this.stats.totalPlaces = this.concoursList.reduce((acc, c) => acc + (c.placesDisponibles || 0), 0);
    this.stats.totalCandidats = this.concoursList.reduce((acc, c) => acc + (c.nombreCandidats || 0), 0);
  }
}
