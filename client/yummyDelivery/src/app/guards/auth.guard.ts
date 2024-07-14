import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from "../service/auth.service";
import {inject} from "@angular/core";

export const authGuard: CanActivateFn = (route, state) => {

  if (inject(AuthService).isUserAuthenticated().getValue()) {
    console.log('Auth Guard: User is authenticated!');
    return true;
  }
  console.log('Auth Guard: You must be authenticated!');
  inject(Router).navigate(['/login']);
  return false;
};
