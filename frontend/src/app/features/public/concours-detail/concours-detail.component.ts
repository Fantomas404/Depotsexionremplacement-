import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ConcoursService } from '../../../core/services/concours.service';
import { Concours } from '../../../models/models';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-concours-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, StatusBadgeComponent],
  templateUrl: './concours-detail.component.html',
  styleUrls: ['./concours-detail.component.css']
})
export class ConcoursDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private concoursService = inject(ConcoursService);
  private cdr = inject(ChangeDetectorRef);

  concours: Concours | null = null;
  loading: boolean = true;
  error: string | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadConcours(+id);
    }
  }

  loadConcours(id: number): void {
    this.loading = true;
    this.cdr.detectChanges();
    this.concoursService.getConcoursById(id).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.concours = res.data;
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err.error?.message || "Impossible de charger les détails du concours.";
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
