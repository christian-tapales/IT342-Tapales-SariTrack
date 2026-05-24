package edu.cit.tapales.saritrack.feature.transaction

import edu.cit.tapales.saritrack.feature.inventory.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TransactionModelsTest {

    @Test
    fun testOrderItemConstructor() {
        val product = Product(1L, 10L, "Soap", "123", 15.0, 5, "Hygiene", null)
        val item = OrderItem(
            id = 200L,
            productId = 1L,
            quantity = 3,
            priceAtSale = 15.0,
            product = product
        )
        assertEquals(200L, item.id)
        assertEquals(1L, item.productId)
        assertEquals(3, item.quantity)
        assertEquals(15.0, item.priceAtSale, 0.0)
        assertEquals(product, item.product)
    }

    @Test
    fun testOrderItemDefaultParameters() {
        val item = OrderItem(productId = 1L, quantity = 2, priceAtSale = 10.0)
        assertNull(item.id)
        assertNull(item.product)
        assertEquals(1L, item.productId)
        assertEquals(2, item.quantity)
    }

    @Test
    fun testOrderItemCopy() {
        val item1 = OrderItem(10L, 1L, 2, 10.0, null)
        val item2 = item1.copy(quantity = 5)
        assertEquals(10L, item2.id)
        assertEquals(5, item2.quantity)
    }

    @Test
    fun testOrderItemEquality() {
        val item1 = OrderItem(1L, 2L, 3, 4.0, null)
        val item2 = OrderItem(1L, 2L, 3, 4.0, null)
        assertEquals(item1, item2)
        assertEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    fun testOrderConstructor() {
        val items = listOf(OrderItem(productId = 1L, quantity = 2, priceAtSale = 15.0))
        val order = Order(
            id = 500L,
            totalAmount = 30.0,
            vendorId = 10L,
            timestamp = "2026-05-19T15:00:00",
            status = "PAID",
            customerId = 8L,
            items = items
        )
        assertEquals(500L, order.id)
        assertEquals(30.0, order.totalAmount, 0.0)
        assertEquals(10L, order.vendorId)
        assertEquals("2026-05-19T15:00:00", order.timestamp)
        assertEquals("PAID", order.status)
        assertEquals(8L, order.customerId)
        assertEquals(items, order.items)
    }

    @Test
    fun testOrderDefaultParameters() {
        val items = listOf(OrderItem(productId = 1L, quantity = 2, priceAtSale = 15.0))
        val order = Order(totalAmount = 30.0, vendorId = 10L, items = items)
        assertNull(order.id)
        assertNull(order.timestamp)
        assertEquals("PAID", order.status)
        assertNull(order.customerId)
        assertEquals(items, order.items)
    }

    @Test
    fun testOrderCopy() {
        val items = listOf(OrderItem(productId = 1L, quantity = 2, priceAtSale = 15.0))
        val order1 = Order(1L, 30.0, 10L, "2026", "PAID", 5L, items)
        val order2 = order1.copy(status = "DEBT")
        assertEquals(1L, order2.id)
        assertEquals("DEBT", order2.status)
    }

    @Test
    fun testOrderEquality() {
        val items = listOf(OrderItem(productId = 1L, quantity = 2, priceAtSale = 15.0))
        val order1 = Order(1L, 30.0, 10L, "2026", "PAID", 5L, items)
        val order2 = Order(1L, 30.0, 10L, "2026", "PAID", 5L, items)
        assertEquals(order1, order2)
    }

    @Test
    fun testOrderToString() {
        val items = listOf(OrderItem(productId = 1L, quantity = 2, priceAtSale = 15.0))
        val order = Order(id = 99L, totalAmount = 30.0, vendorId = 10L, items = items)
        val str = order.toString()
        assert(str.contains("99"))
        assert(str.contains("30.0"))
    }

    @Test
    fun testOrderItemToString() {
        val item = OrderItem(productId = 22L, quantity = 10, priceAtSale = 5.5)
        val str = item.toString()
        assert(str.contains("22"))
        assert(str.contains("10"))
        assert(str.contains("5.5"))
    }

    @Test
    fun testOrderItemInequality() {
        val item1 = OrderItem(1L, 2L, 3, 4.0)
        val item2 = OrderItem(1L, 3L, 3, 4.0)
        assertNotEquals(item1, item2)
    }

    @Test
    fun testOrderInequality() {
        val items = listOf(OrderItem(productId = 1L, quantity = 2, priceAtSale = 15.0))
        val order1 = Order(1L, 30.0, 10L, items = items)
        val order2 = Order(2L, 30.0, 10L, items = items)
        assertNotEquals(order1, order2)
    }
}
