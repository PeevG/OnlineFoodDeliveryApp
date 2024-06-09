import {Component, OnInit} from '@angular/core';
import {ProductComponent} from "../product/product.component";
import {NgForOf} from "@angular/common";
import {ActivatedRoute} from "@angular/router";
import {FoodService} from "../../service/food.service";
import {BeverageService} from "../../service/beverage.service";
import {PageResponse} from "../../shared/model/page-response";
import {NgxPaginationModule} from "ngx-pagination";
import {ErrorHandlingService} from "../../service/error-handling.service";
import {MessageDialogComponent} from "../message-dialog/message-dialog.component";

@Component({
  selector: 'product-list',
  standalone: true,
  imports: [ProductComponent, NgForOf, NgxPaginationModule],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss'
})
export class ProductListComponent implements OnInit {
  foodType: string = '';
  products: PageResponse = {
    content: [],
    size: 0,
    first: true,
    last: true,
    number: 0,
    totalElements: 0,
    totalPages: 0,
    empty: true
  };
  isBeverages: boolean = false;

  constructor(private foodService: FoodService,
              private activeRoute: ActivatedRoute,
              private beverageService: BeverageService,
              private errorHandlingService: ErrorHandlingService) {
  }

  ngOnInit() {
    this.activeRoute.params.subscribe(params => {
      this.foodType = params['foodType'];
      if (params['page'] != null) this.products.number = params['page'];
    });

    this.activeRoute.url.subscribe(urlSegments => {
      this.isBeverages = urlSegments.some(segment => segment.path === 'beverages');
      this.checkAndLoadData();
    })
  }

  checkAndLoadData(): void {
    if (this.isBeverages) this.loadBeverages();
    else this.loadFoods();
  }

  loadFoods(): void {
    this.foodService.getPageOfFoodsByType(this.products.number, this.foodType).subscribe({
      next: (data) => {
        this.products = data.body;
      },
      error: (err) => {
        this.errorHandlingService.showError(err.error.message || "An error occurred while loading foods");
      }
    });
  }

  loadBeverages(): void {
    this.beverageService.getPageOfBeverages(this.products.number).subscribe({
      next: (data) => {
        this.products = data.body;
      },
      error: (err) => {
        this.errorHandlingService.showError(err.error.message || "An error occurred while loading beverages");
      }
    });
  }

  onPageChange(page: number): void {
    this.products.number = page;
    this.checkAndLoadData();
  }
}
