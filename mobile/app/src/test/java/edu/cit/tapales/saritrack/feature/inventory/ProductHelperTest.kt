package edu.cit.tapales.saritrack.feature.inventory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductHelperTest {

    private lateinit var helper: ProductHelper
    private lateinit var sampleProducts: List<Product>

    @Before
    fun setUp() {
        helper = ProductHelper()
        sampleProducts = listOf(
            Product(1L, 1L, "Apple", "111", 10.0, 10, "Fruit", null),
            Product(2L, 1L, "Banana", "222", 5.0, 5, "Fruit", null),
            Product(3L, 1L, "Coke", "333", 20.0, 20, "Drinks", null)
        )
    }

    @Test
    fun `filter should find products by name ignoring case`() {
        val result = helper.filter(sampleProducts, "apple")
        assertEquals(1, result.size)
        assertEquals("Apple", result[0].name)
    }

    @Test
    fun `filter should find products by barcode`() {
        val result = helper.filter(sampleProducts, "222")
        assertEquals(1, result.size)
        assertEquals("Banana", result[0].name)
    }

    @Test
    fun `filter should return all products if query is empty`() {
        val result = helper.filter(sampleProducts, "")
        assertEquals(3, result.size)
    }

    @Test
    fun `filter should return empty list if no match found`() {
        val result = helper.filter(sampleProducts, "Zebra")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `sortByPriceLowToHigh should sort correctly`() {
        val result = helper.sortByPriceLowToHigh(sampleProducts)
        assertEquals(5.0, result[0].price, 0.0)
        assertEquals(10.0, result[1].price, 0.0)
        assertEquals(20.0, result[2].price, 0.0)
    }

    @Test
    fun `sortByPriceHighToLow should sort correctly`() {
        val result = helper.sortByPriceHighToLow(sampleProducts)
        assertEquals(20.0, result[0].price, 0.0)
        assertEquals(10.0, result[1].price, 0.0)
        assertEquals(5.0, result[2].price, 0.0)
    }

    @Test
    fun `sortByName should sort alphabetically`() {
        val result = helper.sortByName(sampleProducts)
        assertEquals("Apple", result[0].name)
        assertEquals("Banana", result[1].name)
        assertEquals("Coke", result[2].name)
    }
}
