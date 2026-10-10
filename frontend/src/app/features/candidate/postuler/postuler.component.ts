import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { finalize, timeout } from 'rxjs';
import { ConcoursService } from '../../../core/services/concours.service';
import { CandidatureService } from '../../../core/services/candidature.service';
import { Concours } from '../../../models/models';

@Component({
  selector: 'app-postuler',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './postuler.component.html',
  styleUrls: ['./postuler.component.css']
})
export class PostulerComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private concoursService = inject(ConcoursService);
  private candidatureService = inject(CandidatureService);

  // State Signals
  concoursList = signal<Concours[]>([]);
  selectedConcoursId = signal<number | null>(null);
  
  cvFile = signal<File | null>(null);
  diplomeFile = signal<File | null>(null);
  photoFile = signal<File | null>(null);

  loading = signal<boolean>(false);
  submitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  
  // Wizard Stepper State
  currentStep = signal<number>(1);
  totalSteps = 3;

  // Computed Values
  selectedConcours = computed(() => {
    const id = this.selectedConcoursId();
    if (!id) return null;
    return this.concoursList().find(c => c.id === id) || null;
  });

  canProceedToStep2 = computed(() => this.selectedConcoursId() !== null);
  canProceedToStep3 = computed(() => this.cvFile() !== null && this.diplomeFile() !== null && this.photoFile() !== null);
  
  cvFileName = computed(() => this.cvFile()?.name || '');
  diplomeFileName = computed(() => this.diplomeFile()?.name || '');
  photoFileName = computed(() => this.photoFile()?.name || '');

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('concoursId');
    if (idParam) {
      this.selectedConcoursId.set(+idParam);
    }
    this.loadConcours();
  }

  loadConcours(): void {
    this.loading.set(true);
    this.concoursService.getPublicConcours().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          const ouverts = res.data.filter(c => c.statut === 'OUVERT');
          this.concoursList.set(ouverts);
          
          if (!this.selectedConcoursId() && ouverts.length > 0) {
            this.selectedConcoursId.set(ouverts[0].id);
          }
        }
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set('Erreur lors du chargement des concours.');
      }
    });
  }

  onConcoursSelect(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedConcoursId.set(Number(selectElement.value));
  }

  onFileSelected(event: Event, type: 'cv' | 'diplome' | 'photo'): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      if (file.size > 10 * 1024 * 1024) {
        this.errorMessage.set("Le fichier dépasse la taille maximale autorisée de 10 Mo.");
        return;
      }

      this.errorMessage.set(null);
      if (type === 'cv') {
        this.cvFile.set(file);
      } else if (type === 'diplome') {
        this.diplomeFile.set(file);
      } else if (type === 'photo') {
        this.photoFile.set(file);
      }
    }
  }

  nextStep(): void {
    if (this.currentStep() === 1 && !this.canProceedToStep2()) {
      this.errorMessage.set("Veuillez sélectionner un concours.");
      return;
    }
    if (this.currentStep() === 2 && !this.canProceedToStep3()) {
      this.errorMessage.set("Veuillez fournir toutes les pièces justificatives obligatoires.");
      return;
    }
    this.errorMessage.set(null);
    if (this.currentStep() < this.totalSteps) {
      this.currentStep.update(s => s + 1);
    }
  }

  prevStep(): void {
    this.errorMessage.set(null);
    if (this.currentStep() > 1) {
      this.currentStep.update(s => s - 1);
    }
  }

  setStep(step: number): void {
    if (step === 2 && !this.canProceedToStep2()) return;
    if (step === 3 && (!this.canProceedToStep2() || !this.canProceedToStep3())) return;
    
    this.errorMessage.set(null);
    this.currentStep.set(step);
  }

  onSubmit(): void {
    const concoursId = this.selectedConcoursId();
    const cv = this.cvFile();
    const diplome = this.diplomeFile();
    const photo = this.photoFile();

    if (!concoursId || !cv || !diplome || !photo) {
      this.errorMessage.set("Dossier incomplet.");
      return;
    }

    this.submitting.set(true);
    this.errorMessage.set(null);

    this.candidatureService.postuler(
      concoursId,
      cv,
      diplome,
      photo
    ).pipe(
      timeout(90000),
      finalize(() => this.submitting.set(false))
    ).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.successMessage.set("Votre candidature a été transmise avec succès ! Redirection...");
          setTimeout(() => {
            this.router.navigate(['/candidate/suivi', res.data.id]);
          }, 1500);
        } else {
          this.errorMessage.set(res.message || "Une erreur est survenue lors de la soumission de votre dossier.");
        }
      },
      error: (err) => {
        if (err.name === 'TimeoutError') {
          this.errorMessage.set("Le délai de traitement a été dépassé. Veuillez réessayer.");
        } else {
          this.errorMessage.set(err.error?.message || "Une erreur est survenue lors de la soumission de votre dossier.");
        }
      }
    });
  }
}
