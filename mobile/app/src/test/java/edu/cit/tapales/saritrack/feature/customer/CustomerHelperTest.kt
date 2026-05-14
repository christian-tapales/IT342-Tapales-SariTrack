package edu.cit.tapales.saritrack.feature.customer

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CustomerHelperTest {

    private lateinit var helper: CustomerHelper
    private lateinit var sampleCustomers: List<Customer>

    @Before
    fun setUp() {
        helper = CustomerHelper()
        sampleCustomers = listOf(
            Customer(1L, 1L, "Juan Luna", "juan@test.com", 200.0),
            Customer(2L, 1L, "Jose Rizal", "jose@test.com", 1200.0),
            Customer(3L, 1L, "Andres Bonifacio", "andres@test.com", 0.0),
            Customer(4L, 1L, "Melchora Aquino", "mel@test.com", 750.0)
        )
    }

    @Test
    fun `getDebtStatus should return Paid for zero debt`() {
        assertEquals("Paid", helper.getDebtStatus(0.0))
        assertEquals("Paid", helper.getDebtStatus(-10.0))
    }

    @Test
    fun `getDebtStatus should return Critical for high debt`() {
        assertEquals("Critical", helper.getDebtStatus(1500.0))
    }

    @Test
    fun `getDebtStatus should return Overdue for medium-high debt`() {
        assertEquals("Overdue", helper.getDebtStatus(800.0))
    }

    @Test
    fun `calculateTotalDebt should sum all debts correctly`() {
        val total = helper.calculateTotalDebt(sampleCustomers)
        assertEquals(2150.0, total, 0.0)
    }

    @Test
    fun `getTopDebtors should return customers with highest debt first`() {
        val top = helper.getTopDebtors(sampleCustomers, 2)
        assertEquals(2, top.size)
        assertEquals("Jose Rizal", top[0].fullName)
        assertEquals("Melchora Aquino", top[1].fullName)
    }
}
