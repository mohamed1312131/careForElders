import type { AbstractControl, ValidationErrors, ValidatorFn, FormGroup } from "@angular/forms"

export class BillValidators {
  // Validate that due date is after bill date
  static dueDateAfterBillDate(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const billDate = formGroup.get("billDate")?.value
      const dueDate = formGroup.get("dueDate")?.value

      if (!billDate || !dueDate) {
        return null
      }

      const billDateObj = new Date(billDate)
      const dueDateObj = new Date(dueDate)

      if (dueDateObj < billDateObj) {
        return { dueDateBeforeBillDate: true }
      }

      return null
    }
  }

  // Validate that total amount matches sum of items
  static totalMatchesItems(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const items = (formGroup.get("items") as FormGroup)?.controls

      if (!items) {
        return null
      }

      let calculatedTotal = 0
      for (const key in items) {
        if (items.hasOwnProperty(key)) {
          const item = items[key]
          const quantity = Number(item.get("quantity")?.value || 0)
          const unitPrice = Number(item.get("unitPrice")?.value || 0)
          calculatedTotal += quantity * unitPrice
        }
      }

      const totalAmount = Number(formGroup.get("totalAmount")?.value || 0)

      if (Math.abs(calculatedTotal - totalAmount) > 0.01) {
        // Allow for small rounding errors
        return { totalMismatch: { expected: calculatedTotal, actual: totalAmount } }
      }

      return null
    }
  }

  // Validate patient ID format
  static patientIdFormat(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null
      }

      const valid = /^[A-Z]{2}\d{6}$/.test(control.value)

      return valid ? null : { invalidPatientIdFormat: true }
    }
  }

  // Validate that at least one item exists
  static hasItems(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const items = formGroup.get("items")?.value

      if (!items || items.length === 0) {
        return { noItems: true }
      }

      return null
    }
  }
}
