import {Component} from '@angular/core';
import {FormsModule, NgForm, ReactiveFormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {UserService} from "../../service/user.service";
import {ErrorHandlingService} from "../../service/error-handling.service";
import {Router} from "@angular/router";
import {AuthService} from "../../service/auth.service";

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.scss'
})
export class ChangePasswordComponent {
  constructor(private userService: UserService,
              private errorHandlingService: ErrorHandlingService,
              private authService: AuthService,
              private router: Router) {
  }

  onFormSubmitted(changePasswordForm: NgForm) {
    let updatePasswordData = {
      oldPassword: changePasswordForm.value.password,
      newPassword: changePasswordForm.value.newPassword,
      repeatNewPassword: changePasswordForm.value.repeatNewPassword
    };
    this.userService.changeUserPassword(updatePasswordData).subscribe({
      next: (res) => {
        this.authService.logout();
        this.router.navigate(['login']);
      }, error: (err) => {
        this.errorHandlingService.showError(err.error.message || 'An error occurred during changing password');
        changePasswordForm.resetForm();
      }
    })
  }
}
