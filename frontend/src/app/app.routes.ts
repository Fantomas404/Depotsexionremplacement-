import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './core/guards/auth.guard';

export const routes: Routes = [

  {
    path: '',
    loadComponent: () => import('./features/public/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'concours',
    loadComponent: () => import('./features/public/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'concours/:id',
    loadComponent: () => import('./features/public/concours-detail/concours-detail.component').then(m => m.ConcoursDetailComponent)
  },
  {
    path: 'resultats',
    loadComponent: () => import('./features/public/results-public/results-public.component').then(m => m.ResultsPublicComponent)
  },

  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },

  {
    path: 'candidate',
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/candidate/dashboard/candidate-dashboard.component').then(m => m.CandidateDashboardComponent)
      },
      {
        path: 'postuler',
        loadComponent: () => import('./features/candidate/postuler/postuler.component').then(m => m.PostulerComponent)
      },
      {
        path: 'postuler/:concoursId',
        loadComponent: () => import('./features/candidate/postuler/postuler.component').then(m => m.PostulerComponent)
      },
      {
        path: 'suivi/:id',
        loadComponent: () => import('./features/candidate/suivi/suivi.component').then(m => m.SuiviComponent)
      },
      {
        path: 'releve/:candidatureId',
        loadComponent: () => import('./features/candidate/releve/releve.component').then(m => m.ReleveComponent)
      }
    ]
  },

  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./features/admin/admin-layout/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/admin/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
      },
      {
        path: 'concours',
        loadComponent: () => import('./features/admin/concours/admin-concours.component').then(m => m.AdminConcoursComponent)
      },
      {
        path: 'candidatures',
        loadComponent: () => import('./features/admin/candidatures/admin-candidatures.component').then(m => m.AdminCandidaturesComponent)
      },
      {
        path: 'notes',
        loadComponent: () => import('./features/admin/notes/admin-notes.component').then(m => m.AdminNotesComponent)
      },
      {
        path: 'deliberation',
        loadComponent: () => import('./features/admin/deliberation/admin-deliberation.component').then(m => m.AdminDeliberationComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./features/admin/users/admin-users.component').then(m => m.AdminUsersComponent)
      }
    ]
  },

  {
    path: '**',
    redirectTo: ''
  }
];
