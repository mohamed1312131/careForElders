import { Component, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Router } from "@angular/router";
import { PatientBillService } from "../patient-bill.service";

@Component({
  selector: "app-patient-bill-list",
  templateUrl: "./patient-bill-list.component.html",
  styleUrls: ["./patient-bill-list.component.scss"],
})
export class PatientBillListComponent implements OnInit {
  // Updated to include serviceType column
  displayedColumns: string[] = [
    "billNumber",
    "patientName",
    "serviceType",
    "billDate",
    "dueDate",
    "totalAmount",
    "balancedAmount",
    "status",
    "actions",
    "paymentActions",
  ];
  
  dataSource = new MatTableDataSource<any>([]);
  isLoading = true;
  noDataMessage = "No bills available. Create a new bill to get started.";

  // Service type mapping for display
  serviceTypeDisplayMap = {
    'DOCTOR_CARE': 'Doctor Care',
    'PARA_MEDICAL_SERVICES': 'Para Medical Services',
    'SUBSCRIPTION': 'Subscription'
  };

  // Service type colors for visual distinction
  serviceTypeColors = {
    'DOCTOR_CARE': '#2196F3',        // Blue
    'PARA_MEDICAL_SERVICES': '#4CAF50', // Green
    'SUBSCRIPTION': '#FF9800'        // Orange
  };

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private patientBillService: PatientBillService,
    private snackBar: MatSnackBar,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadPatientBills();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadPatientBills(): void {
    this.isLoading = true;
    this.patientBillService.getAllBills().subscribe({
      next: (bills) => {
        this.dataSource.data = bills;
        console.log("Fetched bills:", bills);
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error fetching patient bills:", error);
        this.snackBar.open("Failed to load patient bills", "Close", {
          duration: 5000,
          horizontalPosition: "end",
          verticalPosition: "top",
          panelClass: ["error-snackbar"],
        });
        this.isLoading = false;
      },
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  // Filter bills by service type
  filterByServiceType(serviceType: string): void {
    if (serviceType === 'ALL') {
      this.dataSource.filter = '';
    } else {
      this.dataSource.filter = serviceType.toLowerCase();
      this.dataSource.filterPredicate = (data, filter) => {
        return data.serviceType && data.serviceType.toLowerCase().includes(filter);
      };
    }

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  // Reset filter to show all bills
  clearFilters(): void {
    this.dataSource.filter = '';
    this.dataSource.filterPredicate = (data, filter) => {
      const dataStr = Object.keys(data).reduce((currentTerm, key) => {
        return currentTerm + (data as { [key: string]: any })[key] + '◬';
      }, '').toLowerCase();
      const transformedFilter = filter.trim().toLowerCase();
      return dataStr.indexOf(transformedFilter) !== -1;
    };
  }

  getStatusColor(status: string): string {
    if (!status) return "var(--status-neutral)";

    switch (status.toUpperCase()) {
      case "PAID":
        return "var(--status-success)";
      case "PARTIALLY_PAID":
      case "PAYMENT_PLAN":
        return "var(--status-info)";
      case "PENDING":
      case "PENDING_INSURANCE":
      case "SENT":
        return "var(--status-warning)";
      case "OVERDUE":
      case "DISPUTED":
        return "var(--status-danger)";
      case "UNPAID":
        return "var(--status-danger)";
      case "CANCELLED":
      case "ADJUSTED":
        return "var(--status-neutral)";
      default:
        return "var(--status-neutral)";
    }
  }
  getServiceTypeDisplay(serviceType: string): string {
    if (!serviceType) return "N/A"

    switch (serviceType) {
      case "DOCTOR_CARE":
        return "Doctor Care"
      case "PARA_MEDICAL_SERVICES":
        return "Para Medical"
      case "SUBSCRIPTION":
        return "Subscription"
      default:
        return serviceType
    }
  }
  hasServiceType(bill: any): boolean {
    return bill && bill.serviceType && bill.serviceType !== "N/A"
  }
  // Get color for service type
  getServiceTypeColor(serviceType: string): string {
    if (!serviceType) return "#757575"; // Default gray
    return this.serviceTypeColors[serviceType as keyof typeof this.serviceTypeColors] || "#757575";
  }

  // Get service type icon
  getServiceTypeIcon(serviceType: string): string {
    switch (serviceType) {
      case 'DOCTOR_CARE':
        return 'local_hospital';
      case 'PARA_MEDICAL_SERVICES':
        return 'medical_services';
      case 'SUBSCRIPTION':
        return 'subscriptions';
      default:
        return 'receipt';
    }
  }

  formatDate(dateString: string | Date | null): string {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString();
  }

  formatCurrency(amount: number | string | null): string {
    if (amount === null || amount === undefined) return "N/A";

    // Convert string to number if needed
    const numericAmount = typeof amount === "string" ? Number.parseFloat(amount) : amount;

    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(numericAmount);
  }

  calculateBalancedAmount(bill: any): number {
    if (!bill || !bill.totalAmount) return 0;
    
    const totalAmount = typeof bill.totalAmount === "string" ? Number.parseFloat(bill.totalAmount) : bill.totalAmount;
    const paidAmount = bill.paidAmount ? (typeof bill.paidAmount === "string" ? Number.parseFloat(bill.paidAmount) : bill.paidAmount) : 0;
    
    return Math.max(0, totalAmount - paidAmount);
  }

  formatBalancedAmount(bill: any): string {
    const balancedAmount = this.calculateBalancedAmount(bill);
    return this.formatCurrency(balancedAmount);
  }

  getBalancedAmountColor(bill: any): string {
    const balancedAmount = this.calculateBalancedAmount(bill);
    
    if (balancedAmount === 0) {
      return "var(--status-success)"; // Green for fully paid
    } else if (bill.status && bill.status.toUpperCase() === "OVERDUE") {
      return "var(--status-danger)"; // Red for overdue
    } else {
      return "var(--status-warning)"; // Orange for pending balance
    }
  }

  getDisplayValue(value: any, defaultValue = "N/A"): string {
    return value !== null && value !== undefined ? value : defaultValue;
  }

  createNewBill(): void {
    console.log("Navigating to create bill form");
    this.router.navigate(["/user/userProfile/create"]);
  }

  editBill(id: string): void {
    this.router.navigate(["/user/userProfile/edit", id]);
  }

  viewBillDetails(id: string): void {
    this.router.navigate(["/user/userProfile/view", id]);
  }

  // Navigate to payment view
  navigateToPayment(id: string): void {
    console.log("Navigating to payment view for bill:", id);
    this.router.navigate(["/user/userProfile/payment", id]);
  }

  // Check if a bill is eligible for payment
  isPaymentEligible(bill: any): boolean {
    if (!bill || !bill.status) return false;

    const payableStatuses = ["UNPAID", "OVERDUE", "PARTIALLY_PAID", "PENDING", "SENT"];
    return payableStatuses.includes(bill.status.toUpperCase());
  }

  deletePatientBill(id: string): void {
    if (confirm("Are you sure you want to delete this bill?")) {
      this.patientBillService.deleteBill(id).subscribe({
        next: () => {
          this.loadPatientBills();
          this.snackBar.open("Patient bill deleted successfully", "Close", {
            duration: 3000,
            horizontalPosition: "end",
            verticalPosition: "top",
          });
        },
        error: (error) => {
          console.error("Error deleting patient bill:", error);
          this.snackBar.open("Failed to delete patient bill", "Close", {
            duration: 5000,
            horizontalPosition: "end",
            verticalPosition: "top",
            panelClass: ["error-snackbar"],
          });
        },
      });
    }
  }

  downloadPdf(id: string): void {
    console.log("Generating PDF for bill:", id);
    this.patientBillService.generatePdf(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `bill_${id}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      },
      error: (error) => {
        console.error("Error generating PDF:", error);
        this.snackBar.open("Failed to generate PDF", "Close", {
          duration: 5000,
          horizontalPosition: "end",
          verticalPosition: "top",
          panelClass: ["error-snackbar"],
        });
      },
    });
  }

  // Check if bill is eligible for payment based on status
  canProcessPayment(status: string | null | undefined): boolean {
    if (!status) return false;

    const payableStatuses = ["UNPAID", "OVERDUE", "PARTIALLY_PAID", "PENDING", "SENT"];
    return payableStatuses.includes(status.toUpperCase());
  }

  // Get unique service types for filtering
  getUniqueServiceTypes(): string[] {
    const serviceTypes = this.dataSource.data
      .map(bill => bill.serviceType)
      .filter((type, index, array) => type && array.indexOf(type) === index);
    return serviceTypes;
  }

  // Load bills by specific service type
  loadBillsByServiceType(serviceType: string): void {
    this.isLoading = true;
    this.patientBillService.getBillsByServiceType(serviceType).subscribe({
      next: (bills) => {
        this.dataSource.data = bills;
        console.log(`Fetched bills for service type ${serviceType}:`, bills);
        this.isLoading = false;
      },
      error: (error) => {
        console.error(`Error fetching bills for service type ${serviceType}:`, error);
        this.snackBar.open(`Failed to load bills for ${serviceType}`, "Close", {
          duration: 5000,
          horizontalPosition: "end",
          verticalPosition: "top",
          panelClass: ["error-snackbar"],
        });
        this.isLoading = false;
      },
    });
  }
}