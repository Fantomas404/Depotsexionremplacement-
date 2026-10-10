import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ConcoursService } from '../../../core/services/concours.service';
import { DeliberationService } from '../../../core/services/deliberation.service';
import { Concours, Mention, Resultat } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-results-public',
  standalone: true,
  imports: [CommonModule, FormsModule, StatusBadgeComponent],
  templateUrl: './results-public.component.html',
  styleUrls: ['./results-public.component.css']
})
export class ResultsPublicComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private concoursService = inject(ConcoursService);
  private deliberationService = inject(DeliberationService);
  private cdr = inject(ChangeDetectorRef);

  concoursList: Concours[] = [];
  selectedConcoursId: number | null = null;
  selectedMention: Mention | '' = '';
  searchTerm: string = '';

  results: Resultat[] = [];
  filteredResults: Resultat[] = [];

  loadingConcours: boolean = true;
  loadingResults: boolean = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadConcoursList();
  }

  loadConcoursList(): void {
    this.loadingConcours = true;
    this.cdr.detectChanges();
    this.concoursService.getPublicConcours().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.concoursList = res.data;

          const qConcoursId = this.route.snapshot.queryParamMap.get('concoursId');
          if (qConcoursId) {
            this.selectedConcoursId = +qConcoursId;
          } else {

            const published = this.concoursList.find(c => c.statut === 'PUBLIE');
            if (published) {
              this.selectedConcoursId = published.id;
            } else if (this.concoursList.length > 0) {
              this.selectedConcoursId = this.concoursList[0].id;
            }
          }

          if (this.selectedConcoursId) {
            this.loadResults();
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
    this.selectedMention = '';
    this.searchTerm = '';
    this.loadResults();
  }

  loadResults(): void {
    if (!this.selectedConcoursId) return;

    this.loadingResults = true;
    this.error = null;
    this.cdr.detectChanges();

    const mentionParam = this.selectedMention ? this.selectedMention as Mention : undefined;
    this.deliberationService.getPublicResults(this.selectedConcoursId, mentionParam).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.results = res.data;
          this.filterResults();
        } else {
          this.results = [];
          this.filteredResults = [];
        }
        this.loadingResults = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err.error?.message || "Les résultats de ce concours ne sont pas encore publiés ou disponibles.";
        this.results = [];
        this.filteredResults = [];
        this.loadingResults = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterResults(): void {
    let list = this.results;

    if (this.selectedMention) {
      list = list.filter(r => r.mention === this.selectedMention);
    }

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      list = list.filter(r =>
        r.candidatNom.toLowerCase().includes(term) ||
        r.candidatPrenom.toLowerCase().includes(term) ||
        (r.candidatureId && r.candidatureId.toString().includes(term))
      );
    }

    this.filteredResults = list;
  }

  get totalAdmis(): number {
    return this.results.filter(r => r.mention === 'ADMIS').length;
  }

  get totalListeAttente(): number {
    return this.results.filter(r => r.mention === 'LISTE_D_ATTENTE').length;
  }

  get selectedConcours(): Concours | undefined {
    return this.concoursList.find(c => c.id === this.selectedConcoursId);
  }
}
