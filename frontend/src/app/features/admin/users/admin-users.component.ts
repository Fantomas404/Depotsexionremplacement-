import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { Role, User } from '../../../models/models';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.css']
})
export class AdminUsersComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  users: User[] = [];
  filteredUsers: User[] = [];
  selectedRole: Role | '' = '';
  searchTerm: string = '';

  loading: boolean = true;
  saving: boolean = false;
  showModal: boolean = false;
  editMode: boolean = false;
  currentUserId: number | null = null;

  successMessage: string | null = null;
  errorMessage: string | null = null;

  form = {
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    password: '',
    role: 'ROLE_ADMIN' as Role
  };

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.cdr.detectChanges();
    const r = this.selectedRole ? this.selectedRole as Role : undefined;
    this.adminService.getUsers(r).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.users = res.data;
          this.filterUsers();
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Erreur lors du chargement des utilisateurs.";
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filterUsers(): void {
    if (!this.searchTerm.trim()) {
      this.filteredUsers = this.users;
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredUsers = this.users.filter(u =>
        u.nom.toLowerCase().includes(term) ||
        u.prenom.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term) ||
        (u.telephone && u.telephone.includes(term))
      );
    }
  }

  openCreateModal(): void {
    this.editMode = false;
    this.currentUserId = null;
    this.errorMessage = null;
    this.form = {
      nom: '',
      prenom: '',
      email: '',
      telephone: '',
      password: '',
      role: 'ROLE_ADMIN'
    };
    this.showModal = true;
    this.cdr.detectChanges();
  }

  openEditModal(user: User): void {
    this.editMode = true;
    this.currentUserId = user.id;
    this.errorMessage = null;
    this.form = {
      nom: user.nom,
      prenom: user.prenom,
      email: user.email,
      telephone: user.telephone || '',
      password: '',
      role: user.role
    };
    this.showModal = true;
    this.cdr.detectChanges();
  }

  closeModal(): void {
    this.showModal = false;
    this.cdr.detectChanges();
  }

  saveUser(): void {
    if (!this.form.nom || !this.form.prenom || !this.form.email) {
      this.errorMessage = "Veuillez renseigner le nom, prénom et l'adresse email.";
      return;
    }

    if (!this.editMode && !this.form.password) {
      this.errorMessage = "Le mot de passe est obligatoire pour la création d'un compte.";
      return;
    }

    this.saving = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    const action = this.editMode && this.currentUserId ?
      this.adminService.updateUser(this.currentUserId, {
        nom: this.form.nom,
        prenom: this.form.prenom,
        email: this.form.email,
        telephone: this.form.telephone || undefined,
        role: this.form.role,
        password: this.form.password ? this.form.password : undefined
      }) :
      this.adminService.createUser({
        nom: this.form.nom,
        prenom: this.form.prenom,
        email: this.form.email,
        telephone: this.form.telephone || undefined,
        role: this.form.role,
        password: this.form.password
      });

    action.subscribe({
      next: () => {
        this.saving = false;
        this.closeModal();
        this.successMessage = this.editMode ? "Utilisateur mis à jour avec succès !" : "Nouvel utilisateur créé avec succès !";
        this.loadUsers();
        setTimeout(() => {
          this.successMessage = null;
          this.cdr.detectChanges();
        }, 3500);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || "Erreur lors de l'enregistrement de l'utilisateur.";
        this.cdr.detectChanges();
      }
    });
  }

  deleteUser(u: User): void {
    if (confirm(`Voulez-vous vraiment supprimer l'utilisateur "${u.prenom} ${u.nom}" (${u.email}) ?`)) {
      this.adminService.deleteUser(u.id).subscribe({
        next: () => {
          this.loadUsers();
          this.successMessage = `L'utilisateur "${u.prenom} ${u.nom}" a été supprimé avec succès.`;
          this.cdr.detectChanges();
          setTimeout(() => {
            this.successMessage = null;
            this.cdr.detectChanges();
          }, 3500);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || "Impossible de supprimer cet utilisateur (il possède potentiellement des candidatures liées).";
          this.cdr.detectChanges();
        }
      });
    }
  }
}
