package edu.cit.tapales.saritrack.core.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsExpandedTest {

    @Test
    fun testIsValidEmailRejectsMissingDomain() {
        assertFalse(ValidationUtils.isValidEmail("john@"))
    }

    @Test
    fun testIsValidEmailRejectsEmptyExtension() {
        assertFalse(ValidationUtils.isValidEmail("john@domain."))
    }

    @Test
    fun testIsValidEmailRejectsSpecialCharsInDomain() {
        assertFalse(ValidationUtils.isValidEmail("john@dom#ain.com"))
    }

    @Test
    fun testIsValidEmailRejectsMultipleAts() {
        assertFalse(ValidationUtils.isValidEmail("john@doe@domain.com"))
    }

    @Test
    fun testIsValidEmailAcceptsSubdomains() {
        assertTrue(ValidationUtils.isValidEmail("john@mail.domain.co.uk"))
    }

    @Test
    fun testIsStrongPasswordAcceptsExactlySix() {
        assertTrue(ValidationUtils.isStrongPassword("abcdef"))
    }

    @Test
    fun testIsStrongPasswordAcceptsExtremelyLong() {
        assertTrue(ValidationUtils.isStrongPassword("a".repeat(100)))
    }

    @Test
    fun testIsStrongPasswordRejectsBlank() {
        assertFalse(ValidationUtils.isStrongPassword("      "))
    }

    @Test
    fun testIsValidNameAcceptsAccents() {
        assertTrue(ValidationUtils.isValidName("José"))
    }

    @Test
    fun testIsValidNameAcceptsSpaces() {
        assertTrue(ValidationUtils.isValidName("Mary Jane"))
    }

    @Test
    fun testIsValidNameRejectsBlank() {
        assertFalse(ValidationUtils.isValidName("   "))
    }

    @Test
    fun testIsValidNameRejectsEmpty() {
        assertFalse(ValidationUtils.isValidName(""))
    }

    @Test
    fun testIsValidEmailRejectsSpacePrefix() {
        assertFalse(ValidationUtils.isValidEmail(" john@gmail.com"))
    }

    @Test
    fun testIsValidEmailRejectsEmpty() {
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun testIsStrongPasswordRejectsNull() {
        assertFalse(ValidationUtils.isStrongPassword(null))
    }
}
