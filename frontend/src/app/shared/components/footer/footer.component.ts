import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  template: `
    <footer class="bg-[#11111b]/85 backdrop-blur-xl text-slate-400 mt-auto border-t border-white/10 text-sm relative z-20">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 lg:py-16">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-8 lg:gap-12">
          <!-- Brand Column -->
          <div class="lg:col-span-2">
            <div class="flex items-center gap-3 mb-4">
              <div class="w-9 h-9 rounded-xl bg-gradient-to-br from-opti-primary to-opti-accent text-[#1e1e2e] font-black text-base flex items-center justify-center shadow-lg shadow-opti-primary/20">
                SX
              </div>
              <div class="text-xl font-extrabold text-white">
                Sunu<span class="bg-gradient-to-r from-opti-primary to-opti-accent bg-clip-text text-transparent">Xam</span>
              </div>
            </div>
            <p class="text-slate-400 text-sm leading-relaxed mb-4 max-w-sm">
              Plateforme nationale digitalisée de gestion intégrale des concours de recrutement et examens d'État du Sénégal.
            </p>
            <div class="text-xs font-semibold text-opti-primary/90 flex items-center gap-2">
              <span>🇸🇳</span> <em>« Un Peuple - Un But - Une Foi »</em>
            </div>
          </div>

          <!-- Navigation -->
          <div>
            <h4 class="text-white font-bold text-xs mb-4 tracking-widest uppercase">Navigation</h4>
            <ul class="space-y-2.5 text-sm">
              <li><a routerLink="/" class="hover:text-opti-primary transition-colors">Accueil</a></li>
              <li><a routerLink="/concours" class="hover:text-opti-primary transition-colors">Catalogue Concours</a></li>
              <li><a routerLink="/resultats" class="hover:text-opti-primary transition-colors">Consulter Résultats</a></li>
            </ul>
          </div>

          <!-- Espaces -->
          <div>
            <h4 class="text-white font-bold text-xs mb-4 tracking-widest uppercase">Espaces Dédiés</h4>
            <ul class="space-y-2.5 text-sm">
              <li><a routerLink="/candidate/dashboard" class="hover:text-opti-primary transition-colors">Espace Candidat</a></li>
              <li><a routerLink="/auth/register" class="hover:text-opti-primary transition-colors">Créer un compte</a></li>
              <li><a routerLink="/admin/dashboard" class="hover:text-opti-primary transition-colors">Accès Administration</a></li>
            </ul>
          </div>

          <!-- Contact -->
          <div>
            <h4 class="text-white font-bold text-xs mb-4 tracking-widest uppercase">Assistance</h4>
            <ul class="space-y-2.5 text-xs text-slate-400">
              <li class="flex items-center gap-2"><span>📍</span> Dakar, République du Sénégal</li>
              <li class="flex items-center gap-2"><span>📧</span> contact&#64;OptiConcours.sn</li>
              <li class="flex items-center gap-2"><span>📞</span> +221 33 800 00 00</li>
            </ul>
          </div>
        </div>
      </div>

      <div class="border-t border-white/5 py-6 text-xs text-slate-500">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4">
          <span>&copy; 2026 OptiConcours • République du Sénégal. Tous droits réservés.</span>
          <span class="text-slate-400">Projet de Soutenance L3 Génie Logiciel • Spring Boot 3 & Angular</span>
        </div>
      </div>
    </footer>
  `
})
export class FooterComponent {}
