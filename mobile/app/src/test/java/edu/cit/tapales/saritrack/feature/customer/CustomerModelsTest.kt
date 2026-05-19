package edu.cit.tapales.saritrack.feature.customer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CustomerModelsTest {

    @Test
    fun testCustomerDefaultParameters() {
        val customer = Customer(vendorId = 100L, fullName = "Juan Dela Cruz", email = "juan@example.com")
        assertNull(customer.id)
        assertEquals(100L, customer.vendorId)
        assertEquals("Juan Dela Cruz", customer.fullName)
        assertEquals("juan@example.com", customer.email)
        assertEquals(0.0, customer.currentDebt, 0.0)
        assertEquals("Unpaid", customer.status)
        assertNull(customer.lastUpdate)
    }

    @Test
    fun testCustomerExplicitParameters() {
        val customer = Customer(
            id = 50L,
            vendorId = 100L,
            fullName = "Maria Clara",
            email = "maria@example.com",
            currentDebt = 500.25,
            status = "Overdue",
            lastUpdate = "2026-05-19T10:00:00"
        )
        assertEquals(50L, customer.id)
        assertEquals(500.25, customer.currentDebt, 0.0)
        assertEquals("Overdue", customer.status)
        assertEquals("2026-05-19T10:00:00", customer.lastUpdate)
    }

    @Test
    fun testCustomerCopy() {
        val customer1 = Customer(vendorId = 100L, fullName = "Juan", email = "juan@example.com")
        val customer2 = customer1.copy(currentDebt = 120.50, status = "Partially Paid")
        assertEquals("Juan", customer2.fullName)
        assertEquals(120.50, customer2.currentDebt, 0.0)
        assertEquals("Partially Paid", customer2.status)
    }

    @Test
    fun testCustomerEquality() {
        val customer1 = Customer(1L, 100L, "Juan", "juan@example.com", 50.0, "Unpaid", "now")
        val customer2 = Customer(1L, 100L, "Juan", "juan@example.com", 50.0, "Unpaid", "now")
        assertEquals(customer1, customer2)
        assertEquals(customer1.hashCode(), customer2.hashCode())
    }

    @Test
    fun testCustomerInequality() {
        val customer1 = Customer(1L, 100L, "Juan", "juan@example.com")
        val customer2 = Customer(2L, 100L, "Juan", "juan@example.com")
        assertNotEquals(customer1, customer2)
    }

    @Test
    fun testDebtPaymentDefaultParameters() {
        val payment = DebtPayment(customerId = 5L, amount = 150.0)
        assertNull(payment.id)
        assertEquals(5L, payment.customerId)
        assertEquals(150.0, payment.amount, 0.0)
        assertNull(payment.timestamp)
    }

    @Test
    fun testDebtPaymentExplicitParameters() {
        val payment = DebtPayment(id = 12L, customerId = 5L, amount = 150.0, timestamp = "2026-05-19T12:00:00")
        assertEquals(12L, payment.id)
        assertEquals("2026-05-19T12:00:00", payment.timestamp)
    }

    @Test
    fun testDebtPaymentCopy() {
        val payment1 = DebtPayment(id = 1L, customerId = 5L, amount = 150.0)
        val payment2 = payment1.copy(amount = 200.0)
        assertEquals(1L, payment2.id)
        assertEquals(200.0, payment2.amount, 0.0)
    }

    @Test
    fun testDebtPaymentEquality() {
        val payment1 = DebtPayment(1L, 5L, 100.0, "2026")
        val payment2 = DebtPayment(1L, 5L, 100.0, "2026")
        assertEquals(payment1, payment2)
    }

    @Test
    fun testCustomerToString() {
        val customer = Customer(fullName = "Gabriel Silang", vendorId = 10L, email = null)
        val str = customer.toString()
        assert(str.contains("Gabriel Silang"))
        assert(str.contains("10"))
    }

    @Test
    fun testDebtPaymentToString() {
        val payment = DebtPayment(customerId = 8L, amount = 999.0)
        val str = payment.toString()
        assert(str.contains("8"))
        assert(str.contains("999.0"))
    }

    @Test
    fun testDebtPaymentInequality() {
        val payment1 = DebtPayment(1L, 5L, 100.0)
        val payment2 = DebtPayment(1L, 6L, 100.0)
        assertNotEquals(payment1, payment2)
    }
}
