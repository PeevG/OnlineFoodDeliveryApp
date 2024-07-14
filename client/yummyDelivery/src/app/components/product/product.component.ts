import {Component, Input} from '@angular/core';
import {CurrencyPipe, NgIf} from "@angular/common";
import {Beverage} from "../../shared/model/beverage";
import {Food} from "../../shared/model/food";
import {ShoppingCartService} from "../../service/shopping-cart.service";
import {ErrorHandlingService} from "../../service/error-handling.service";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {MessageDialogComponent} from "../message-dialog/message-dialog.component";
import {AuthService} from "../../service/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'product',
  standalone: true,
  imports: [
    CurrencyPipe,
    NgIf,
    MatIcon
  ],
  templateUrl: './product.component.html',
  styleUrl: './product.component.scss'
})
export class ProductComponent {
  @Input() product: Food | Beverage | null | undefined;
  @Input() isBeverage: boolean = false;

  constructor(private cartService: ShoppingCartService,
              private errorHandlingService: ErrorHandlingService,
              private dialog: MatDialog,
              private authService: AuthService,
              private router: Router) {
  }

  get food(): Food | null {
    return this.isBeverage ? null : this.product as Food;
  }

  get beverage(): Beverage | null {
    return this.isBeverage ? this.product as Beverage : null;
  }

  addProductToCart(id: number | null | undefined) {
    if (this.authService.isUserAuthenticated().getValue()) {
      this.cartService.addItemToCart(id).subscribe({
        next: () => {
          let dialogRef = this.dialog.open(MessageDialogComponent, {
            data: {
              message: "Product is added to your cart",
              buttonText: 'Close'
            }
          });
          setTimeout(() => {
            dialogRef.close();
          }, 800);
        }, error: (err) => {
          this.errorHandlingService.showError(err.error.message || "An error occurred while adding cart item");
        }
      })
    } else {
      this.router.navigate(['login']);
    }
  }
}
