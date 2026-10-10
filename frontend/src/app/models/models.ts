export type Role = 'ROLE_CANDIDAT' | 'ROLE_ADMIN';

export type StatutConcours = 'BROUILLON' | 'OUVERT' | 'CLOTURE' | 'EN_DELIBERATION' | 'PUBLIE';

export type StatutCandidature = 'EN_ATTENTE' | 'DOSSIER_COMPLET' | 'REJETE' | 'EN_DELIBERATION' | 'DELIBERE';

export type Mention = 'ADMIS' | 'LISTE_D_ATTENTE' | 'NON_ADMIS';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface User {
  id: number;
  email: string;
  nom: string;
  prenom: string;
  telephone?: string;
  role: Role;
  createdAt?: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface Epreuve {
  id?: number;
  intitule: string;
  coefficient: number;
  dureeMinutes: number;
}

export interface Concours {
  id: number;
  titre: string;
  description?: string;
  conditions?: string;
  dateDebut: string;
  dateFin: string;
  dateDeliberation?: string;
  placesDisponibles: number;
  statut: StatutConcours;
  createdAt: string;
  nombreCandidats: number;
  totalCoefficients: number;
  epreuves: Epreuve[];
}

export interface ConcoursRequest {
  titre: string;
  description?: string;
  conditions?: string;
  dateDebut: string;
  dateFin: string;
  dateDeliberation?: string;
  placesDisponibles: number;
  statut?: StatutConcours;
  epreuves: Epreuve[];
}

export interface Note {
  id?: number;
  epreuveId: number;
  epreuveIntitule: string;
  coefficient: number;
  valeurNote: number;
  notePonderee: number;
}

export interface Resultat {
  id: number;
  candidatureId: number;
  candidatId: number;
  candidatNom: string;
  candidatPrenom: string;
  candidatEmail: string;
  candidatTelephone?: string;
  concoursId: number;
  concoursTitre: string;
  moyenneGenerale: number;
  rang: number;
  mention: Mention;
  estPublie: boolean;
  datePublication?: string;
  notes: Note[];
}

export interface Candidature {
  id: number;
  candidatId: number;
  candidatNom: string;
  candidatPrenom: string;
  candidatEmail: string;
  candidatTelephone?: string;
  concoursId: number;
  concoursTitre: string;
  cvUrl: string;
  diplomeUrl: string;
  photoUrl: string;
  statut: StatutCandidature;
  motifRejet?: string;
  dateSoumission: string;
  notes?: Note[];
  resultat?: Resultat;
}

export interface NoteSaisieItem {
  epreuveId: number;
  valeurNote: number;
}

export interface NotesBatchRequest {
  candidatureId: number;
  notes: NoteSaisieItem[];
}

export interface DeliberationConfig {
  seuilAdmission?: number;
  seuilListeAttente?: number;
  placesMaxAdmis?: number;
}

export interface DeliberationSummary {
  concoursId: number;
  concoursTitre: string;
  totalCandidatsEvalues: number;
  totalAdmis: number;
  totalListeAttente: number;
  totalRefuses: number;
  meilleureMoyenne: number;
  plusFaibleMoyenne: number;
  moyenneGeneralePromotion: number;
  estPublie: boolean;
  classements: Resultat[];
}

export interface DashboardStats {
  totalConcours: number;
  concoursOuverts: number;
  totalCandidatsInscrits: number;
  totalCandidatures: number;
  candidaturesEnAttente: number;
  candidaturesValidees: number;
  candidaturesRejetees: number;
  resultatsPublies: number;
  derniersConcours: Concours[];
  dernieresCandidatures: Candidature[];
}
