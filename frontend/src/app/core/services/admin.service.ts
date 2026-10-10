import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ApiResponse,
  Candidature,
  Concours,
  ConcoursRequest,
  DashboardStats,
  DeliberationConfig,
  DeliberationSummary,
  NotesBatchRequest,
  Role,
  StatutCandidature,
  StatutConcours,
  User
} from '../../models/models';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly API_URL = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<ApiResponse<DashboardStats>> {
    return this.http.get<ApiResponse<DashboardStats>>(`${this.API_URL}/dashboard/stats`);
  }

  getAllConcours(): Observable<ApiResponse<Concours[]>> {
    return this.http.get<ApiResponse<Concours[]>>(`${this.API_URL}/concours`);
  }

  createConcours(req: ConcoursRequest): Observable<ApiResponse<Concours>> {
    return this.http.post<ApiResponse<Concours>>(`${this.API_URL}/concours`, req);
  }

  updateConcours(id: number, req: ConcoursRequest): Observable<ApiResponse<Concours>> {
    return this.http.put<ApiResponse<Concours>>(`${this.API_URL}/concours/${id}`, req);
  }

  updateConcoursStatut(id: number, statut: StatutConcours): Observable<ApiResponse<void>> {
    const params = new HttpParams().set('statut', statut);
    return this.http.patch<ApiResponse<void>>(`${this.API_URL}/concours/${id}/statut`, null, { params });
  }

  deleteConcours(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/concours/${id}`);
  }

  getCandidatures(concoursId?: number, statut?: StatutCandidature): Observable<ApiResponse<Candidature[]>> {
    let params = new HttpParams();
    if (concoursId) params = params.set('concoursId', concoursId.toString());
    if (statut) params = params.set('statut', statut);
    return this.http.get<ApiResponse<Candidature[]>>(`${this.API_URL}/candidatures`, { params });
  }

  updateCandidatureStatut(id: number, statut: StatutCandidature, motifRejet?: string): Observable<ApiResponse<Candidature>> {
    return this.http.patch<ApiResponse<Candidature>>(`${this.API_URL}/candidatures/${id}/statut`, {
      statut,
      motifRejet
    });
  }

  getMatrixSaisieNotes(concoursId: number): Observable<ApiResponse<Candidature[]>> {
    return this.http.get<ApiResponse<Candidature[]>>(`${this.API_URL}/concours/${concoursId}/saisie-notes`);
  }

  enregistrerNotes(concoursId: number, batch: NotesBatchRequest[]): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.API_URL}/concours/${concoursId}/saisie-notes`, batch);
  }

  calculerDeliberation(concoursId: number, config?: DeliberationConfig): Observable<ApiResponse<DeliberationSummary>> {
    return this.http.post<ApiResponse<DeliberationSummary>>(`${this.API_URL}/concours/${concoursId}/calculer-deliberation`, config || {});
  }

  getDeliberation(concoursId: number): Observable<ApiResponse<DeliberationSummary>> {
    return this.http.get<ApiResponse<DeliberationSummary>>(`${this.API_URL}/concours/${concoursId}/deliberation`);
  }

  publierResultats(concoursId: number): Observable<ApiResponse<DeliberationSummary>> {
    return this.http.post<ApiResponse<DeliberationSummary>>(`${this.API_URL}/concours/${concoursId}/publier-resultats`, {});
  }

  getUsers(role?: Role): Observable<ApiResponse<User[]>> {
    let params = new HttpParams();
    if (role) params = params.set('role', role);
    return this.http.get<ApiResponse<User[]>>(`${this.API_URL}/users`, { params });
  }

  getUserById(id: number): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.API_URL}/users/${id}`);
  }

  createUser(user: { email: string; password: string; nom: string; prenom: string; telephone?: string; role: Role }): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.API_URL}/users`, user);
  }

  updateUser(id: number, user: { email: string; password?: string; nom: string; prenom: string; telephone?: string; role: Role }): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.API_URL}/users/${id}`, user);
  }

  deleteUser(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/users/${id}`);
  }
}
