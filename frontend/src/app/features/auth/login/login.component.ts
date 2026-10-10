import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  credentials = {
    email: '',
    password: ''
  };

  loading: boolean = false;
  errorMessage: string | null = null;

  onSubmit(): void {
    if (!this.credentials.email || !this.credentials.password) {
      this.errorMessage = "Veuillez remplir tous les champs obligatoires.";
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    this.authService.login(this.credentials).subscribe({
      next: (res) => {
        this.loading = false;
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        if (returnUrl) {
          this.router.navigateByUrl(returnUrl);
        } else if (res.data?.user.role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.router.navigate(['/candidate/dashboard']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || "Identifiants invalides. Veuillez vérifier votre email et mot de passe.";
      }
    });
  }

  fillDemoAdmin(): void {
    this.credentials.email = 'admin@OptiConcours.sn';
    this.credentials.password = 'Admin@123';
  }

  fillDemoCandidate(): void {
    this.credentials.email = 'moussa.fall@OptiConcours.sn';
    this.credentials.password = 'Candidat@123';
  }
}
