package com.bagusrizki.delapanjayafarm

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val ID_USER_KEY = stringPreferencesKey("id_user")
        val LEVEL_USER_KEY = stringPreferencesKey("level_user")
    }

    // Save user data
    suspend fun saveUser(idUser: String, levelUser: String) {
        dataStore.edit { preferences ->
            preferences[ID_USER_KEY] = idUser
            preferences[LEVEL_USER_KEY] = levelUser
        }
    }

    // Get user data
    val userIdFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ID_USER_KEY]
    }

    val userLevelFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[LEVEL_USER_KEY]
    }

    // Clear user data
    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}