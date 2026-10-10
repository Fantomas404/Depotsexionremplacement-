import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Mention, Resultat } from '../../models/models';

@Injectable({
  providedIn: 'root'
})
export class DeliberationService {
  private readonly API_URL = 'http://localhost:8080/api/resultats';

  constructor(private http: HttpClient) {}

  getPublicResults(concoursId: number, mention?: Mention): Observable<ApiResponse<Resultat[]>> {
    let params = new HttpParams().set('concoursId', concoursId.toString());
    if (mention) {
      params = params.set('mention', mention);
    }
    return this.http.get<ApiResponse<Resultat[]>>(`${this.API_URL}/public`, { params });
  }

  getMyResultat(candidatureId: number): Observable<ApiResponse<Resultat>> {
    return this.http.get<ApiResponse<Resultat>>(`${this.API_URL}/my/${candidatureId}`);
  }
}
