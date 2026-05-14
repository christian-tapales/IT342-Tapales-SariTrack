package edu.cit.tapales.saritrack.core.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun `isValidEmail should return true for valid emails`() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"))
        assertTrue(ValidationUtils.isValidEmail("user.name@sub.domain.ph"))
    }

    @Test
    fun `isValidEmail should return false for invalid formats`() {
        assertFalse(ValidationUtils.isValidEmail("plainstyle"))
        assertFalse(ValidationUtils.isValidEmail("@missingname.com"))
        assertFalse(ValidationUtils.isValidEmail("name@domain"))
        assertFalse(ValidationUtils.isValidEmail(null))
    }

    @Test
    fun `isStrongPassword should return true for 6 or more chars`() {
        assertTrue(ValidationUtils.isStrongPassword("123456"))
        assertTrue(ValidationUtils.isStrongPassword("password123"))
    }

    @Test
    fun `isStrongPassword should return false for short passwords`() {
        assertFalse(ValidationUtils.isStrongPassword("12345"))
        assertFalse(ValidationUtils.isStrongPassword(""))
        assertFalse(ValidationUtils.isStrongPassword(null))
    }

    @Test
    fun `isValidName should return true for names with 2 or more chars`() {
        assertTrue(ValidationUtils.isValidName("Al"))
        assertTrue(ValidationUtils.isValidName("Juan Luna"))
    }

    @Test
    fun `isValidName should return false for empty or short names`() {
        assertFalse(ValidationUtils.isValidName("A"))
        assertFalse(ValidationUtils.isValidName(""))
        assertFalse(ValidationUtils.isValidName(null))
    }
}
