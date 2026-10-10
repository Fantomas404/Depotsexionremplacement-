import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CandidatureService } from '../../../core/services/candidature.service';
import { AuthService } from '../../../core/services/auth.service';
import { Candidature } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-candidate-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, StatusBadgeComponent],
  templateUrl: './candidate-dashboard.component.html',
  styleUrls: ['./candidate-dashboard.component.css']
})
export class CandidateDashboardComponent implements OnInit {
  private candidatureService = inject(CandidatureService);
  private cdr = inject(ChangeDetectorRef);
  authService = inject(AuthService);

  candidatures: Candidature[] = [];
  loading: boolean = true;

  ngOnInit(): void {
    this.loadCandidatures();
  }

  loadCandidatures(): void {
    this.loading = true;
    this.cdr.detectChanges();
    this.candidatureService.getMyCandidatures().subscribe({
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
}
