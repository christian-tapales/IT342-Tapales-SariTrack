package edu.cit.tapales.saritrack.core.auth

import edu.cit.tapales.saritrack.core.api.AuthInterceptor
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AuthInterceptorTest {

    private lateinit var mockSessionManager: SessionManager
    private lateinit var authInterceptor: AuthInterceptor
    private lateinit var mockChain: Interceptor.Chain
    private lateinit var mockRequest: Request

    @Before
    fun setUp() {
        mockSessionManager = mock()
        authInterceptor = AuthInterceptor(mockSessionManager)
        mockChain = mock()
        mockRequest = Request.Builder()
            .url("https://api.test.com/")
            .build()
    }

    @Test
    fun `intercept should add bearer token when token exists`() {
        // Arrange
        val testToken = "test_jwt_token"
        whenever(mockSessionManager.fetchAuthToken()).thenReturn(testToken)
        whenever(mockChain.request()).thenReturn(mockRequest)
        
        // Use a mock response to satisfy the intercept return type
        val mockResponse: Response = mock()
        whenever(mockChain.proceed(any())).thenAnswer {
            val interceptedRequest = it.getArgument<Request>(0)
            // Assert that header was added
            assertEquals("Bearer $testToken", interceptedRequest.header("Authorization"))
            assertEquals("true", interceptedRequest.header("ngrok-skip-browser-warning"))
            mockResponse
        }

        // Act
        authInterceptor.intercept(mockChain)
    }

    @Test
    fun `intercept should NOT add bearer token when token is null`() {
        // Arrange
        whenever(mockSessionManager.fetchAuthToken()).thenReturn(null)
        whenever(mockChain.request()).thenReturn(mockRequest)
        
        val mockResponse: Response = mock()
        whenever(mockChain.proceed(any())).thenAnswer {
            val interceptedRequest = it.getArgument<Request>(0)
            // Assert that header was NOT added
            assertEquals(null, interceptedRequest.header("Authorization"))
            assertEquals("true", interceptedRequest.header("ngrok-skip-browser-warning"))
            mockResponse
        }

        // Act
        authInterceptor.intercept(mockChain)
    }
}
