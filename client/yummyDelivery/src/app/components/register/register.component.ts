import {Component} from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {AuthService} from "../../service/auth.service";
import {SignUpRequest} from "../../shared/model/sign-up-request";
import { CommonModule} from "@angular/common";
import {MatDialog} from "@angular/material/dialog";
import {ErrorHandlingService} from "../../service/error-handling.service";
import {MessageDialogComponent} from "../message-dialog/message-dialog.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule, CommonModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  emailValue: string = '';
  constructor(private authService: AuthService,
              private dialog: MatDialog,
              private router: Router,
              private errorHandlingService: ErrorHandlingService) {
  }

  onFormSubmitted(form: NgForm) {
    const signUpData: SignUpRequest = {
      email: form.value.email,
      password: form.value.password,
      firstName: form.value.firstName,
      lastName: form.value.lastName,
      city: form.value.city,
      streetName: form.value.streetName,
      streetNumber: form.value.streetNumber,
      phoneNumber: form.value.phoneNumber
    };

    this.authService.signUp(signUpData)
      .subscribe({
      next: (res) => {
        this.dialog.open(MessageDialogComponent, {
          data: {
            message: 'Your registration was successful!',
            buttonText: 'OK'
          }
        }).afterClosed().subscribe(() => {
          this.router.navigate(['/login']);
        });
      },
      error: (err) => {
        this.errorHandlingService.showError(err.error.message || 'An error occurred during registration');
        form.controls['password'].reset();
      }
    });
  }
  updateEmail(value: string): void {
    if(value !== null){
      this.emailValue = value.toLowerCase();
    }
  }
}
