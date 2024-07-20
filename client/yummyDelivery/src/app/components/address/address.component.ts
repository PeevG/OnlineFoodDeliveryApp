import {Component, OnInit} from '@angular/core';
import {Address} from "../../shared/model/address";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {AddressService} from "../../service/address.service";
import {PageResponse} from "../../shared/model/page-response";
import {Router, RouterLink} from "@angular/router";
import {NgxPaginationModule} from "ngx-pagination";
import {MatIcon} from "@angular/material/icon";
import {MatRadioButton, MatRadioGroup} from "@angular/material/radio";

@Component({
  selector: 'app-address',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    RouterLink,
    NgxPaginationModule,
    MatIcon,
    MatRadioGroup,
    MatRadioButton
  ],
  templateUrl: './address.component.html',
  styleUrl: './address.component.scss'
})
export class AddressComponent implements OnInit {
  addresses: PageResponse<Address> = {
    content: [],
    size: 0,
    first: true,
    last: true,
    number: 0,
    totalElements: 0,
    totalPages: 0,
    empty: true
  };
  addressesArray: Address[] = [];
  selectedAddressId: number | undefined;
  isPickAddressForm: boolean = false;

  constructor(private addressService: AddressService,
              private router: Router) {
  }

  ngOnInit() {
    if(this.router.url.endsWith('pick-address')){
      console.log('Ends with pick-address: ' + this.router.url.endsWith('pick-address'));
      this.isPickAddressForm = true;
    }
    this.loadUserAddresses();
  }

  loadUserAddresses() {
    this.addressService.getUserAddresses(this.addresses.number).subscribe({
      next: (res) => {
        this.addressesArray = res.body.content;
        this.addresses = res.body;
        console.log(this.addresses);
      },
      error: (error) => {
        console.error('Error fetching foods:', error);
      }
    });
  }

  onDeleteAddress(id: number): void {
    this.addressService.deleteAddress(id).subscribe(() => {
      this.loadUserAddresses();
    });
    console.log(`Address with id:${id} is deleted`);
  }

  onPageChange(page: number): void {
    this.addresses.number = page;
    this.loadUserAddresses();
  }

  selectAddress(addressId: number): void {
    this.selectedAddressId = addressId;
  }

  confirmAddress() {
    this.router.navigate([`/order-preview/${this.selectedAddressId}`]);
  }
}
