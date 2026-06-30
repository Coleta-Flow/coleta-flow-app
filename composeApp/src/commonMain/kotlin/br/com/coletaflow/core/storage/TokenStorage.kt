package br.com.coletaflow.core.storage

interface TokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}
