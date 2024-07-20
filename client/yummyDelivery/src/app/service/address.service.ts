import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {PageResponse} from "../shared/model/page-response";
import {ApiResponse} from "../shared/model/api-response";
import {AddAddressRequest} from "../shared/model/add-address-request";
import {Address} from "../shared/model/address";

@Injectable({
  providedIn: 'root'
})
export class AddressService {

  private apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  getUserAddresses(page: number) {
    const params = new HttpParams()
      .set('page', page);
    return this.http.get<ApiResponse<PageResponse<Address>>>(`${this.apiBaseUrl}/addresses`, {params});
  }

  getAddressById(addressId: number) {
    return this.http.get<ApiResponse<any>>(`${this.apiBaseUrl}/addresses/${addressId}`);
  }

  deleteAddress(id: number) {
    return this.http.delete(`${this.apiBaseUrl}/addresses/${id}`);
  }

  addAddress(addRequestData: AddAddressRequest) {
    return this.http.post<ApiResponse<any>>(`${this.apiBaseUrl}/addresses`, addRequestData);
  }

  updateAddress(updateData: AddAddressRequest, addressId: number) {
    return this.http.put(`${this.apiBaseUrl}/addresses/${addressId}`, updateData);
  }
}
