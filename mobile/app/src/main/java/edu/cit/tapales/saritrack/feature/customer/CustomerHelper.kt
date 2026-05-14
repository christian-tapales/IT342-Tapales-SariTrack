package edu.cit.tapales.saritrack.feature.customer

class CustomerHelper {
    fun getDebtStatus(debt: Double): String {
        return when {
            debt <= 0 -> "Paid"
            debt < 500 -> "Active"
            debt < 1000 -> "Overdue"
            else -> "Critical"
        }
    }

    fun calculateTotalDebt(customers: List<Customer>): Double {
        return customers.sumOf { it.currentDebt }
    }

    fun getTopDebtors(customers: List<Customer>, limit: Int = 3): List<Customer> {
        return customers.filter { it.currentDebt > 0 }
            .sortedByDescending { it.currentDebt }
            .take(limit)
    }
}
