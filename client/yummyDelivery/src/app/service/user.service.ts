import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApiResponse} from "../shared/model/api-response";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  changeUserPassword(updatePasswordData: { oldPassword: string; newPassword: string; repeatNewPassword: string;}) {
    return this.http.put<ApiResponse<void>>(`${this.apiBaseUrl}/user`, updatePasswordData);
  }
}
