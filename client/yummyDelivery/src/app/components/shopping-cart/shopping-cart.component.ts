import {Component, OnInit} from '@angular/core';
import {ShoppingCartService} from "../../service/shopping-cart.service";
import {ErrorHandlingService} from "../../service/error-handling.service";
import {CurrencyPipe, NgForOf} from "@angular/common";
import {NgxPaginationModule} from "ngx-pagination";
import {Router, RouterLink} from "@angular/router";
import {ProductComponent} from "../product/product.component";
import {CartItem} from "../../shared/model/cart-item";
import {MatDialog} from "@angular/material/dialog";
import {MessageDialogComponent} from "../message-dialog/message-dialog.component";
import {MatIcon} from "@angular/material/icon";
import {isEmpty} from "rxjs";

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [
    NgForOf,
    NgxPaginationModule,
    RouterLink,
    ProductComponent,
    CurrencyPipe,
    MatIcon
  ],
  templateUrl: './shopping-cart.component.html',
  styleUrl: './shopping-cart.component.scss'
})
export class ShoppingCartComponent implements OnInit {
  cartItems: CartItem[] = [];
  cartPrice: number = 0;

  constructor(private cartService: ShoppingCartService,
              private errorHandlingService: ErrorHandlingService,
              private dialog: MatDialog,
              private router: Router) {
  }

  ngOnInit(): void {
    this.loadUserCart();
  }

  showEmptyCartDialog() {
    this.dialog.open(MessageDialogComponent, {
      data: {
        message: "Your cart is empty",
        buttonText: 'OK'
      }
    }).afterClosed().subscribe(() => {
      this.router.navigate(['/']);
    });
  }

  loadUserCart() {
    this.cartService.getUserCart().subscribe({
      next: (res) => {
        console.log("loadUserCart() called");
        this.cartItems = res.body.items;
        this.cartPrice = res.body.cartPrice;
      }, error: (err) => {
        this.errorHandlingService.showError(err.error.message() || "An error occurred while loading shopping cart");
      }
    })
  }

  removeCartItem(cartItemId: number) {
    this.cartService.removeCartItem(cartItemId).subscribe({
      next: (res) => {
        this.cartService.getUserCart().subscribe({
          next: (res) => {
            if (res.body.items.length === 0) {
              this.showEmptyCartDialog();
            } else {
              this.loadUserCart();
            }
          }
        })
      }, error: (err) => {
        this.errorHandlingService.showError(err.error.message() || "Error during deleting a item from shopping cart")
      }
    })
  }
}
