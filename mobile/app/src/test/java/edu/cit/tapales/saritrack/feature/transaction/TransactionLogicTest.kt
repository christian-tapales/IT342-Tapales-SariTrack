package edu.cit.tapales.saritrack.feature.transaction

import edu.cit.tapales.saritrack.feature.inventory.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionLogicTest {

    @Test
    fun `order should correctly hold items and total amount`() {
        val items = listOf(
            OrderItem(productId = 1L, quantity = 2, priceAtSale = 25.0),
            OrderItem(productId = 2L, quantity = 1, priceAtSale = 50.0)
        )
        
        val order = Order(
            id = 101L,
            totalAmount = 100.0,
            vendorId = 1L,
            items = items
        )

        assertEquals(100.0, order.totalAmount, 0.0)
        assertEquals(2, order.items.size)
        assertEquals("PAID", order.status)
    }

    @Test
    fun `order status defaults to PAID when not specified`() {
        val order = Order(
            totalAmount = 50.0,
            vendorId = 1L,
            items = emptyList()
        )
        assertEquals("PAID", order.status)
    }

    @Test
    fun `order items should map correctly to their product IDs`() {
        val items = listOf(
            OrderItem(productId = 55L, quantity = 1, priceAtSale = 10.0),
            OrderItem(productId = 99L, quantity = 5, priceAtSale = 20.0)
        )
        
        assertEquals(55L, items[0].productId)
        assertEquals(99L, items[1].productId)
        assertEquals(5, items[1].quantity)
    }
}
