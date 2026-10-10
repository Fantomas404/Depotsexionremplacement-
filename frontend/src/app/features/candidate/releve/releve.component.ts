import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DeliberationService } from '../../../core/services/deliberation.service';
import { Resultat } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-releve',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './releve.component.html',
  styleUrls: ['./releve.component.css']
})
export class ReleveComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private deliberationService = inject(DeliberationService);
  private cdr = inject(ChangeDetectorRef);

  resultat: Resultat | null = null;
  loading: boolean = true;
  error: string | null = null;

  ngOnInit(): void {
    const candId = this.route.snapshot.paramMap.get('candidatureId');
    if (candId) {
      this.loadResultat(+candId);
    }
  }

  loadResultat(candidatureId: number): void {
    this.loading = true;
    this.cdr.detectChanges();
    this.deliberationService.getMyResultat(candidatureId).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.resultat = res.data;
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err.error?.message || "Relevé de notes non disponible.";
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  get totalCoefficients(): number {
    if (!this.resultat?.notes) return 0;
    return this.resultat.notes.reduce((acc, n) => acc + n.coefficient, 0);
  }

  get totalPointsPonderes(): number {
    if (!this.resultat?.notes) return 0;
    return this.resultat.notes.reduce((acc, n) => acc + n.notePonderee, 0);
  }

  print(): void {
    window.print();
  }
}
