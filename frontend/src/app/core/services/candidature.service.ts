import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Candidature } from '../../models/models';

@Injectable({
  providedIn: 'root'
})
export class CandidatureService {
  private readonly API_URL = 'http://localhost:8080/api/candidatures';

  constructor(private http: HttpClient) {}

  postuler(concoursId: number, cvFile: File, diplomeFile: File, photoFile: File): Observable<ApiResponse<Candidature>> {
    const formData = new FormData();
    formData.append('concoursId', concoursId.toString());
    formData.append('cv', cvFile);
    formData.append('diplome', diplomeFile);
    formData.append('photo', photoFile);

    return this.http.post<ApiResponse<Candidature>>(this.API_URL, formData);
  }

  getMyCandidatures(): Observable<ApiResponse<Candidature[]>> {
    return this.http.get<ApiResponse<Candidature[]>>(`${this.API_URL}/my`);
  }

  getCandidatureById(id: number): Observable<ApiResponse<Candidature>> {
    return this.http.get<ApiResponse<Candidature>>(`${this.API_URL}/${id}`);
  }
}
