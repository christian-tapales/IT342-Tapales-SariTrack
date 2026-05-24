package edu.cit.tapales.saritrack.feature.inventory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductModelsTest {

    @Test
    fun testProductConstructor() {
        val product = Product(
            id = 1L,
            vendorId = 10L,
            name = "Sardines",
            barcode = "480001611111",
            price = 22.50,
            stockQuantity = 24,
            category = "Canned Goods",
            imageUrl = "http://supabase.com/sardines.png"
        )
        assertEquals(1L, product.id)
        assertEquals(10L, product.vendorId)
        assertEquals("Sardines", product.name)
        assertEquals("480001611111", product.barcode)
        assertEquals(22.50, product.price, 0.0)
        assertEquals(24, product.stockQuantity)
        assertEquals("Canned Goods", product.category)
        assertEquals("http://supabase.com/sardines.png", product.imageUrl)
    }

    @Test
    fun testProductNullableFields() {
        val product = Product(
            id = null,
            vendorId = 10L,
            name = null,
            barcode = null,
            price = 0.0,
            stockQuantity = 0,
            category = null,
            imageUrl = null
        )
        assertNull(product.id)
        assertNull(product.name)
        assertNull(product.barcode)
        assertNull(product.category)
        assertNull(product.imageUrl)
    }

    @Test
    fun testProductCopy() {
        val product1 = Product(1L, 10L, "Sardines", "48000", 22.50, 10, "Canned", null)
        val product2 = product1.copy(stockQuantity = 15, price = 24.0)
        assertEquals("Sardines", product2.name)
        assertEquals(15, product2.stockQuantity)
        assertEquals(24.0, product2.price, 0.0)
    }

    @Test
    fun testProductEquality() {
        val product1 = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        val product2 = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        assertEquals(product1, product2)
        assertEquals(product1.hashCode(), product2.hashCode())
    }

    @Test
    fun testProductInequality() {
        val product1 = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        val product2 = Product(1L, 10L, "Z", "B", 1.0, 1, "C", "D")
        assertNotEquals(product1, product2)
    }

    @Test
    fun testProductToString() {
        val product = Product(3L, 5L, "Coke", "123", 15.0, 5, "Beverages", null)
        val str = product.toString()
        assert(str.contains("Coke"))
        assert(str.contains("Beverages"))
        assert(str.contains("15.0"))
    }

    @Test
    fun testProductModifyVendorId() {
        val product1 = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        val product2 = product1.copy(vendorId = 20L)
        assertEquals(20L, product2.vendorId)
        assertEquals(1L, product2.id)
    }

    @Test
    fun testProductModifyBarcode() {
        val product1 = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        val product2 = product1.copy(barcode = "99999")
        assertEquals("99999", product2.barcode)
    }

    @Test
    fun testProductModifyCategory() {
        val product1 = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        val product2 = product1.copy(category = "NewCategory")
        assertEquals("NewCategory", product2.category)
    }

    @Test
    fun testProductModifyImageUrl() {
        val product1 = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        val product2 = product1.copy(imageUrl = "http://newurl")
        assertEquals("http://newurl", product2.imageUrl)
    }

    @Test
    fun testProductHashcodeStability() {
        val product = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        val initialHash = product.hashCode()
        assertEquals(initialHash, product.hashCode())
    }

    @Test
    fun testProductDiffIdsNotEqual() {
        val product1 = Product(1L, 10L, "A", "B", 1.0, 1, "C", "D")
        val product2 = Product(null, 10L, "A", "B", 1.0, 1, "C", "D")
        assertNotEquals(product1, product2)
    }
}
