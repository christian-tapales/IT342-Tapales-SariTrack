package edu.cit.tapales.saritrack.feature.pos

import edu.cit.tapales.saritrack.feature.inventory.Product
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CartManagerTest {

    private lateinit var cartManager: CartManager
    private lateinit var productA: Product
    private lateinit var productB: Product

    @Before
    fun setUp() {
        cartManager = CartManager()
        productA = Product(1L, 1L, "Coke", "123", 25.0, 10, "Drinks", null)
        productB = Product(2L, 1L, "Bread", "456", 5.0, 5, "Food", null)
    }

    @Test
    fun `addItem should increase quantity and reflect in totals`() {
        cartManager.addItem(productA)
        cartManager.addItem(productA)
        
        assertEquals(2, cartManager.getTotalItems())
        assertEquals(50.0, cartManager.getTotalPrice(), 0.01)
        assertEquals(2, cartManager.getItems()[productA])
    }

    @Test
    fun `addItem should return false when out of stock`() {
        val outOfStockProduct = Product(3L, 1L, "Sold Out", "789", 10.0, 0, "Cat", null)
        
        assertFalse(cartManager.addItem(outOfStockProduct))
        assertEquals(0, cartManager.getTotalItems())
    }

    @Test
    fun `addItem should not exceed stock quantity`() {
        val limitedStockProduct = Product(4L, 1L, "Limited", "000", 10.0, 2, "Cat", null)
        
        assertTrue(cartManager.addItem(limitedStockProduct))
        assertTrue(cartManager.addItem(limitedStockProduct))
        assertFalse(cartManager.addItem(limitedStockProduct)) // 3rd should fail
        
        assertEquals(2, cartManager.getTotalItems())
    }

    @Test
    fun `removeItem should decrease quantity or remove item`() {
        cartManager.addItem(productA)
        cartManager.addItem(productA)
        
        cartManager.removeItem(productA)
        assertEquals(1, cartManager.getTotalItems())
        
        cartManager.removeItem(productA)
        assertTrue(cartManager.isEmpty())
    }

    @Test
    fun `clear should empty the cart`() {
        cartManager.addItem(productA)
        cartManager.addItem(productB)
        
        cartManager.clear()
        
        assertTrue(cartManager.isEmpty())
        assertEquals(0, cartManager.getTotalItems())
        assertEquals(0.0, cartManager.getTotalPrice(), 0.0)
    }
}
