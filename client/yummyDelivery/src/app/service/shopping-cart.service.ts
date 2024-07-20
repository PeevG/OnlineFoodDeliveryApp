import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ApiResponse} from "../shared/model/api-response";
import {environment} from "../../environments/environment";
import {ShoppingCart} from "../shared/model/shopping-cart";
import {CartItem} from "../shared/model/cart-item";

@Injectable({
  providedIn: 'root'
})
export class ShoppingCartService {
  private apiBaseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {
  }

  getUserCart() {
    return this.http.get<ApiResponse<ShoppingCart<CartItem>>>(`${this.apiBaseUrl}/shoppingCart`);
  }

  removeCartItem(cartItemId: number) {
    return this.http.delete<ApiResponse<any>>(`${this.apiBaseUrl}/shoppingCart/${cartItemId}`);
  }

  addItemToCart(productId: number | null | undefined) {
    return this.http.post<ApiResponse<any>>(`${this.apiBaseUrl}/menu/addToCart/${productId}`, productId);
  }
}
