import { Component } from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {NgIf} from "@angular/common";
import {AuthService} from "../../service/auth.service";
import {ErrorHandlingService} from "../../service/error-handling.service";
import {Router, RouterLink} from "@angular/router";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  emailValue: string = '';

  constructor(private authService: AuthService,
              private errorHandlingService: ErrorHandlingService,
              private router: Router) {}

  onFormSubmitted(form: NgForm) {
    const signInData = {
      email: form.value.email,
      password: form.value.password
    };

    this.authService.signIn(signInData).subscribe( {
      next:() => {
        this.router.navigate(['/foods/pizza']);
      },
      error: (err) => {
        this.errorHandlingService.showError(err.error.message || 'An error occurred during registration');
        form.reset();
      }
    })
  }
  updateEmail(value: string): void {
    if(value !== null){
      this.emailValue = value.toLowerCase();
    }
  }
}
