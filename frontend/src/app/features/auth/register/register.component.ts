import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  formData = {
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    password: '',
    confirmPassword: ''
  };

  loading: boolean = false;
  errorMessage: string | null = null;

  onSubmit(): void {
    if (!this.formData.nom || !this.formData.prenom || !this.formData.email || !this.formData.telephone || !this.formData.password) {
      this.errorMessage = "Tous les champs obligatoires doivent être renseignés.";
      return;
    }

    if (this.formData.password.length < 6) {
      this.errorMessage = "Le mot de passe doit comporter au moins 6 caractères.";
      return;
    }

    if (this.formData.password !== this.formData.confirmPassword) {
      this.errorMessage = "Les mots de passe ne correspondent pas.";
      return;
    }

    this.loading = true;
    this.errorMessage = null;

    this.authService.register({
      nom: this.formData.nom,
      prenom: this.formData.prenom,
      email: this.formData.email,
      telephone: this.formData.telephone,
      password: this.formData.password
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/candidate/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || "Erreur lors de l'inscription. Veuillez vérifier vos données.";
      }
    });
  }
}
