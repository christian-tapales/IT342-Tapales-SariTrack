package edu.cit.tapales.saritrack.feature.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AuthModelsTest {

    @Test
    fun testRegisterRequestProperties() {
        val request = RegisterRequest("Juan", "juan@gmail.com", "pass123")
        assertEquals("Juan", request.name)
        assertEquals("juan@gmail.com", request.email)
        assertEquals("pass123", request.password)
    }

    @Test
    fun testRegisterRequestCopy() {
        val request1 = RegisterRequest("Juan", "juan@gmail.com", "pass123")
        val request2 = request1.copy(name = "Pedro")
        assertEquals("Pedro", request2.name)
        assertEquals("juan@gmail.com", request2.email)
    }

    @Test
    fun testRegisterRequestEquality() {
        val request1 = RegisterRequest("Juan", "juan@gmail.com", "pass123")
        val request2 = RegisterRequest("Juan", "juan@gmail.com", "pass123")
        assertEquals(request1, request2)
        assertEquals(request1.hashCode(), request2.hashCode())
    }

    @Test
    fun testLoginRequestProperties() {
        val request = LoginRequest("juan@gmail.com", "pass123")
        assertEquals("juan@gmail.com", request.email)
        assertEquals("pass123", request.password)
    }

    @Test
    fun testLoginRequestCopy() {
        val request1 = LoginRequest("juan@gmail.com", "pass123")
        val request2 = request1.copy(password = "newpass")
        assertEquals("juan@gmail.com", request2.email)
        assertEquals("newpass", request2.password)
    }

    @Test
    fun testLoginRequestEquality() {
        val request1 = LoginRequest("juan@gmail.com", "pass123")
        val request2 = LoginRequest("juan@gmail.com", "pass123")
        assertEquals(request1, request2)
        assertEquals(request1.hashCode(), request2.hashCode())
    }

    @Test
    fun testLoginResponseProperties() {
        val response = LoginResponse(1L, "Juan", "juan@gmail.com", "VENDOR", "jwt-token-xyz")
        assertEquals(1L, response.id)
        assertEquals("Juan", response.name)
        assertEquals("juan@gmail.com", response.email)
        assertEquals("VENDOR", response.role)
        assertEquals("jwt-token-xyz", response.token)
    }

    @Test
    fun testLoginResponseCopy() {
        val response1 = LoginResponse(1L, "Juan", "juan@gmail.com", "VENDOR", "jwt-token-xyz")
        val response2 = response1.copy(token = "new-token")
        assertEquals(1L, response2.id)
        assertEquals("new-token", response2.token)
    }

    @Test
    fun testLoginResponseEquality() {
        val response1 = LoginResponse(1L, "Juan", "juan@gmail.com", "VENDOR", "jwt-token-xyz")
        val response2 = LoginResponse(1L, "Juan", "juan@gmail.com", "VENDOR", "jwt-token-xyz")
        assertEquals(response1, response2)
        assertEquals(response1.hashCode(), response2.hashCode())
    }

    @Test
    fun testLoginResponseInequality() {
        val response1 = LoginResponse(1L, "Juan", "juan@gmail.com", "VENDOR", "jwt-token-xyz")
        val response2 = LoginResponse(2L, "Juan", "juan@gmail.com", "VENDOR", "jwt-token-xyz")
        assertNotEquals(response1, response2)
    }

    @Test
    fun testRegisterRequestToString() {
        val request = RegisterRequest("Juan", "juan@gmail.com", "pass")
        val str = request.toString()
        assert(str.contains("Juan"))
        assert(str.contains("juan@gmail.com"))
    }

    @Test
    fun testLoginResponseToString() {
        val response = LoginResponse(1L, "Juan", "juan@gmail.com", "VENDOR", "token")
        val str = response.toString()
        assert(str.contains("jwt-token-xyz").not())
        assert(str.contains("VENDOR"))
    }
}
