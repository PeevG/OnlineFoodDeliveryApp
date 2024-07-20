import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {SignUpRequest} from "../shared/model/sign-up-request";
import {environment} from "../../environments/environment";
import {ApiResponse} from "../shared/model/api-response";
import {jwtDecode} from "jwt-decode";
import {BehaviorSubject, tap} from "rxjs";
import {Router} from "@angular/router";

interface JwtPayload {
  roles: { authority: string }[];
  exp: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiBaseUrl = environment.apiBaseUrl;
  private readonly JWT_TOKEN = 'JWT_TOKEN';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private rolesSubject = new BehaviorSubject<string[]>([]);

  constructor(private http: HttpClient,
              private router: Router) {
    this.checkToken();
  }

  signUp(signUpData: SignUpRequest) {
    console.log(signUpData);
    return this.http.post<ApiResponse<SignUpRequest>>(`${this.apiBaseUrl}/auth/register`, signUpData);
  }

  signIn(signInData: {
    email: string,
    password: string
  }) {
    return this.http.post<ApiResponse<any>>(`${this.apiBaseUrl}/auth/login`, signInData).pipe(
      tap((response) =>
        this.doLoginUser(signInData.email, response.body.accessToken)));
  }

  private doLoginUser(email: string, token: any) {
    localStorage.setItem(this.JWT_TOKEN, token);
    this.isAuthenticatedSubject.next(true);
    this.rolesSubject.next(this.getRoles());
  }

  logout() {
    localStorage.removeItem(this.JWT_TOKEN);
    this.isAuthenticatedSubject.next(false);
    this.rolesSubject.next([]);
    this.router.navigate(['/']);
  }

  getRoles(): string[] {
    let token = localStorage.getItem(this.JWT_TOKEN);
    if (token) {
      let decoded: JwtPayload = jwtDecode(token);
      return decoded.roles.map(role => role.authority);
    }
    return [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  getRolesObservable(): BehaviorSubject<string[]> {
    return this.rolesSubject;
  }

  isUserAuthenticated(): BehaviorSubject<boolean> {
    this.checkToken();
    return this.isAuthenticatedSubject;
  }

  private checkToken() {
    const token = localStorage.getItem(this.JWT_TOKEN);
    if (token) {
      const decoded: JwtPayload = jwtDecode(token);
      //const currentTime = Date.now() / 1000;
      if (decoded.roles && decoded.roles.length > 0 && !this.isTokenExpired(token)) {
        this.isAuthenticatedSubject.next(true);
        this.rolesSubject.next(decoded.roles.map(role => role.authority));
      } else {
        this.isAuthenticatedSubject.next(false);
        this.rolesSubject.next([]);
      }
    } else {
      this.isAuthenticatedSubject.next(false);
      this.rolesSubject.next([]);
    }
  }

  isTokenExpired(token: string): boolean {
    const decoded = jwtDecode<JwtPayload>(token);
    const exp = decoded.exp * 1000; // Convert to milliseconds
    return Date.now() > exp;
  }
}
