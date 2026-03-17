package com.grocart.first.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("GroCartPrefs", Context.MODE_PRIVATE)


    /**
     * Saves user session details to SharedPreferences.
     * @param userId ID of the logged-in user.
     * @param username Username.
     */
    fun saveUserSession(userId: Long, username: String) {
        prefs.edit {
            putLong("USER_ID", userId)
            putString("USERNAME", username)
        }
    }


    /**
     * Gets the current user ID.
     * @return User ID or -1 if not logged in.
     */
    fun getUserId(): Long = prefs.getLong("USER_ID", -1L)

    /**
     * Gets the current user name.
     * @return Username or null if not logged in.
     */
    fun getUsername(): String? = prefs.getString("USERNAME", null)


    /**
     * Clears all session data, effectively logging out the user.
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}