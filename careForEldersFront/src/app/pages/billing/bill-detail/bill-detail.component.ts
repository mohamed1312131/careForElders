// src/app/pages/billing/bill-detail/bill-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BillService } from 'src/app/services/PayBill/bill.service';

@Component({
  selector: 'app-bill-detail',
  templateUrl: './bill-detail.component.html',
  styleUrls: ['./bill-detail.component.css']
})
export class BillDetailComponent implements OnInit {
  bill: any;
  isLoading = true;
  billId: string;

  constructor(
    private route: ActivatedRoute,
    private billService: BillService,
    private router: Router
  ) {
    this.billId = this.route.snapshot.paramMap.get('id') || '';
  }

  ngOnInit(): void {
    this.loadBillDetails();
  }

  loadBillDetails() {
    this.billService.getBill(+this.billId).subscribe({
      next: (bill) => {
        this.bill = bill;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.router.navigate(['/billing/bills']);
      }
    });
  }

  navigateToPayment() {
    this.router.navigate(['/billing', this.billId, 'pay']);
  }
}