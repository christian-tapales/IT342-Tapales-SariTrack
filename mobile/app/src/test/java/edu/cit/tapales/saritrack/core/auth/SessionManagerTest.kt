package edu.cit.tapales.saritrack.core.auth

import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*

class SessionManagerTest {

    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        mockPrefs = mock()
        mockEditor = mock()
        
        // Mock the editor flow: prefs.edit().putXXX().apply()
        whenever(mockPrefs.edit()).thenReturn(mockEditor)
        whenever(mockEditor.putString(any(), any())).thenReturn(mockEditor)
        whenever(mockEditor.putLong(any(), any())).thenReturn(mockEditor)
        whenever(mockEditor.putBoolean(any(), any())).thenReturn(mockEditor)
        whenever(mockEditor.clear()).thenReturn(mockEditor)
        
        sessionManager = SessionManager(mockPrefs)
    }

    @Test
    fun `saveAuthToken should store token and set login status`() {
        val token = "abc-123"
        
        sessionManager.saveAuthToken(token)
        
        verify(mockEditor).putString("jwt_token", token)
        verify(mockEditor).putBoolean("is_logged_in", true)
        verify(mockEditor, times(2)).apply()
    }

    @Test
    fun `fetchAuthToken should return stored token`() {
        whenever(mockPrefs.getString("jwt_token", null)).thenReturn("saved-token")
        
        val token = sessionManager.fetchAuthToken()
        
        assertEquals("saved-token", token)
    }

    @Test
    fun `isLoggedIn should return correct status`() {
        whenever(mockPrefs.getBoolean("is_logged_in", false)).thenReturn(true)
        assertTrue(sessionManager.isLoggedIn())
        
        whenever(mockPrefs.getBoolean("is_logged_in", false)).thenReturn(false)
        assertFalse(sessionManager.isLoggedIn())
    }

    @Test
    fun `logout should clear all preferences`() {
        sessionManager.logout()
        
        verify(mockEditor).clear()
        verify(mockEditor).apply()
    }

    @Test
    fun `saveUserDetail should store all user info`() {
        sessionManager.saveUserDetail(1L, "test@test.com", "VENDOR", "Juan")
        
        verify(mockEditor).putLong("user_id", 1L)
        verify(mockEditor).putString("user_email", "test@test.com")
        verify(mockEditor).putString("user_role", "VENDOR")
        verify(mockEditor).putString("user_name", "Juan")
        verify(mockEditor, atLeastOnce()).apply()
    }
}
