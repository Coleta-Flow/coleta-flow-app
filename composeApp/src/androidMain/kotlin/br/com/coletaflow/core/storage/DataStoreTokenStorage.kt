package br.com.coletaflow.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("coletaflow_prefs")

class DataStoreTokenStorage(private val context: Context) : TokenStorage {

    private val tokenKey = stringPreferencesKey("auth_token")

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs -> prefs[tokenKey] = token }
    }

    override suspend fun getToken(): String? =
        context.dataStore.data.map { it[tokenKey] }.firstOrNull()

    override suspend fun clearToken() {
        context.dataStore.edit { prefs -> prefs.remove(tokenKey) }
    }
}
