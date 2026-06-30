package br.com.coletaflow.domain.repositories

import br.com.coletaflow.core.result.Result

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
)

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthUser>
    suspend fun logout()
    suspend fun getStoredToken(): String?
    suspend fun isLoggedIn(): Boolean
}
