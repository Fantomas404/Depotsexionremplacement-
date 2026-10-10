import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Concours } from '../../models/models';

@Injectable({
  providedIn: 'root'
})
export class ConcoursService {
  private readonly API_URL = 'http://localhost:8080/api/concours';

  constructor(private http: HttpClient) {}

  getPublicConcours(): Observable<ApiResponse<Concours[]>> {
    return this.http.get<ApiResponse<Concours[]>>(this.API_URL);
  }

  getConcoursById(id: number): Observable<ApiResponse<Concours>> {
    return this.http.get<ApiResponse<Concours>>(`${this.API_URL}/${id}`);
  }
}
