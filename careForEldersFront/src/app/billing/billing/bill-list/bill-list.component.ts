import { Component, OnInit } from '@angular/core';
import { BillService } from '../services/bill.service';
import { Bill } from '../models/bill';

@Component({
  selector: 'app-bill-list',
  templateUrl: './bill-list.component.html'
})
export class BillListComponent implements OnInit {
  bills: Bill[] = [];
  filteredBills: Bill[] = [];
  searchTerm = '';

  constructor(private billService: BillService) {}

  ngOnInit(): void {
    this.loadBills();
  }

  loadBills(): void {
    this.billService.getAll().subscribe(bills => {
      this.bills = bills;
      this.filteredBills = [...bills];
    });
  }

  applyFilter(): void {
    this.filteredBills = this.bills.filter(bill => 
      bill.patientName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      bill.id.toString().includes(this.searchTerm)
    );
  }
}