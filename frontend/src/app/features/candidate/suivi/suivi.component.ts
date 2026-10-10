import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CandidatureService } from '../../../core/services/candidature.service';
import { Candidature } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-suivi',
  standalone: true,
  imports: [CommonModule, RouterLink, StatusBadgeComponent],
  templateUrl: './suivi.component.html',
  styleUrls: ['./suivi.component.css']
})
export class SuiviComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private candidatureService = inject(CandidatureService);
  private cdr = inject(ChangeDetectorRef);

  candidature: Candidature | null = null;
  loading: boolean = true;
  error: string | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadCandidature(+id);
    }
  }

  loadCandidature(id: number): void {
    this.loading = true;
    this.cdr.detectChanges();
    this.candidatureService.getCandidatureById(id).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.candidature = res.data;
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err.error?.message || "Impossible de charger le suivi de cette candidature.";
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  get stepIndex(): number {
    if (!this.candidature) return 1;
    switch (this.candidature.statut) {
      case 'EN_ATTENTE': return 1;
      case 'DOSSIER_COMPLET': return 2;
      case 'EN_DELIBERATION': return 3;
      case 'DELIBERE': return 4;
      case 'REJETE': return -1;
      default: return 1;
    }
  }
}
