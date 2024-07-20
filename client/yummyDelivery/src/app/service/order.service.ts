import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApiResponse} from "../shared/model/api-response";
import {environment} from "../../environments/environment";
import {PageResponse} from "../shared/model/page-response";
import {Order} from "../shared/model/order";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiBaseUrl = environment.apiBaseUrl;
  private orderSubject = new BehaviorSubject<Order | null>(null);
  currentOrder = this.orderSubject.asObservable();

  setOrder(order: Order): void {
    this.orderSubject.next(order);
  }

  constructor(private http: HttpClient) {
  }

  createOrder(addressId: number) {
    return this.http.post<ApiResponse<any>>(`${this.apiBaseUrl}/orders/${addressId}`, addressId);
  }

  getUserOrders() {
    return this.http.get<ApiResponse<PageResponse<Order>>>(`${this.apiBaseUrl}/orders`);
  }
}
