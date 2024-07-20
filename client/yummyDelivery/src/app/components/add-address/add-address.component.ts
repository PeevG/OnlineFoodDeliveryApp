import {Component} from '@angular/core';
import {NgIf} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {AddressService} from "../../service/address.service";
import {AddAddressRequest} from "../../shared/model/add-address-request";
import {ErrorHandlingService} from "../../service/error-handling.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-add-address',
  standalone: true,
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './add-address.component.html',
  styleUrl: './add-address.component.scss'
})
export class AddAddressComponent {
  constructor(private addressService: AddressService,
              private errorHandlingService: ErrorHandlingService,
              private router: Router) {
  }
  onFormSubmitted(form: NgForm) {
     const addAddressData: AddAddressRequest = {
      city: form.value.city,
      streetName: form.value.streetName,
      streetNumber: form.value.streetNumber,
      phoneNumber: form.value.phoneNumber
    };
    this.addressService.addAddress(addAddressData).subscribe( {
      next:() => {
        this.router.navigate(['/addresses']);
      },
      error:(err) => {
        this.errorHandlingService.showError(err.error.message || 'An error occurred during adding new address');
      }
    })
  }
}
