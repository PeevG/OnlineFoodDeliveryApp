import {Component, OnInit} from '@angular/core';
import {CurrencyPipe, NgForOf, NgIf} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {ShoppingCartService} from "../../service/shopping-cart.service";
import {CartItem} from "../../shared/model/cart-item";
import {ErrorHandlingService} from "../../service/error-handling.service";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {AddressService} from "../../service/address.service";
import {Address} from "../../shared/model/address";
import {OrderService} from "../../service/order.service";
import {MessageDialogComponent} from "../message-dialog/message-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [
    CurrencyPipe,
    MatIcon,
    NgForOf,
    RouterLink,
    NgIf
  ],
  templateUrl: './order-details.component.html',
  styleUrl: './order-details.component.scss'
})
export class OrderDetailsComponent implements OnInit {
  cartItems: CartItem[] = [];
  cartPrice: number = 0;
  orderAddress: Address = {
    id: 0,
    city: '',
    streetName: '',
    streetNumber: '',
    phoneNumber: ''
  };
  orderCost: number = 0.00;
  isOrderHistory: boolean = false;

  constructor(private cartService: ShoppingCartService,
              private orderService: OrderService,
              private errorHandlingService: ErrorHandlingService,
              private addressService: AddressService,
              private route: ActivatedRoute,
              private dialog: MatDialog,
              private router: Router) {
  }
  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const orderIdParam = params.get('orderId');
      const orderId = orderIdParam ? +orderIdParam : null;

      if (orderId) {
        this.orderService.currentOrder.subscribe(order => {
          if (order && order.id === orderId) {
            this.isOrderHistory = true;
            this.cartItems = order.orderedProducts;
            this.orderAddress = order.deliveryAddress;
            this.orderCost = order.orderCost;
          }
        })
      } else {
        this.loadUserCart();
        this.getAddressById();
      }
    });
  }

  loadUserCart() {
    this.cartService.getUserCart().subscribe({
      next: (res) => {
        console.log(res);
        this.cartItems = res.body.items;
        this.cartPrice = res.body.cartPrice;
      }, error: (err) => {
        this.errorHandlingService.showError(err.error.message() || "An error occurred while loading cart items");
      }
    })
  }

  getAddressById() {
    let addressId: number = 0;
    this.route.params.subscribe(params => {
      addressId = params['addressId'];
    });
    this.addressService.getAddressById(addressId).subscribe({
      next: (res) => {
        this.orderAddress = res.body;
      }, error: (err) => {
        this.errorHandlingService.showError(err.error.message() || "An error occurred during loading address");
      }
    })
  }

  createOrder() {
    this.orderService.createOrder(this.orderAddress.id).subscribe({
      next: (res) => {
        let dialogRef = this.dialog.open(MessageDialogComponent, {
          data: {
            message: "Your order is received! Wait for call",
            buttonText: 'OK'
          }
        });
        setTimeout(() => {
          dialogRef.close();
        }, 5000);

        dialogRef.afterClosed().subscribe(() => {
          this.router.navigate(['/']);
        })
      }, error: (err) => {
        this.errorHandlingService.showError(err.error.message() || "An error occurred during creating order");
      }
    })
  }
}
