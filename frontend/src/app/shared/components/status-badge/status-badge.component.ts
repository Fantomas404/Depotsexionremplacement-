import { Component, input, computed } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  template: `
    <span [class]="badgeClasses()">
      <span class="w-1.5 h-1.5 rounded-full shadow-sm" [class]="dotClass()"></span>
      {{ label() }}
    </span>
  `
})
export class StatusBadgeComponent {
  readonly status = input<string>('');
  readonly type = input<'concours' | 'candidature' | 'mention'>('concours');

  readonly label = computed(() => {
    switch (this.status()) {
      case 'OUVERT': return 'Inscriptions Ouvertes';
      case 'CLOTURE': return 'Inscriptions Clôturées';
      case 'EN_DELIBERATION': return 'En Délibération';
      case 'PUBLIE': return 'Résultats Publiés';
      case 'BROUILLON': return 'Brouillon';

      case 'EN_ATTENTE': return 'En Attente de Vérification';
      case 'DOSSIER_COMPLET': return 'Dossier Validé';
      case 'REJETE': return 'Dossier Rejeté';
      case 'DELIBERE': return 'Résultat Prêt';

      case 'ADMIS': return 'ADMIS(E)';
      case 'LISTE_D_ATTENTE': return 'LISTE D\'ATTENTE';
      case 'NON_ADMIS': return 'NON ADMIS(E)';

      default: return this.status() || 'Inconnu';
    }
  });

  readonly badgeClasses = computed(() => {
    const s = this.status();
    const base = 'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold backdrop-blur-md border transition-all duration-200 shadow-sm';

    switch (s) {
      case 'OUVERT':
      case 'DOSSIER_COMPLET':
      case 'ADMIS':
        return `${base} bg-emerald-500/15 text-emerald-300 border-emerald-500/30`;

      case 'EN_ATTENTE':
      case 'EN_DELIBERATION':
      case 'LISTE_D_ATTENTE':
        return `${base} bg-amber-500/15 text-amber-300 border-amber-500/30`;

      case 'REJETE':
      case 'NON_ADMIS':
      case 'CLOTURE':
        return `${base} bg-rose-500/15 text-rose-300 border-rose-500/30`;

      case 'PUBLIE':
        return `${base} bg-opti-accent/15 text-opti-accent border-opti-accent/30`;

      default:
        return `${base} bg-white/10 text-slate-300 border-white/15`;
    }
  });

  readonly dotClass = computed(() => {
    const s = this.status();
    switch (s) {
      case 'OUVERT':
      case 'DOSSIER_COMPLET':
      case 'ADMIS':
        return 'bg-emerald-400 shadow-emerald-400/50';

      case 'EN_ATTENTE':
      case 'EN_DELIBERATION':
      case 'LISTE_D_ATTENTE':
        return 'bg-amber-400 shadow-amber-400/50';

      case 'REJETE':
      case 'NON_ADMIS':
      case 'CLOTURE':
        return 'bg-rose-400 shadow-rose-400/50';

      case 'PUBLIE':
        return 'bg-opti-accent shadow-opti-accent/50';

      default:
        return 'bg-slate-400';
    }
  });
}
