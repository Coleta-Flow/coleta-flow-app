package br.com.coletaflow.data.auth

import br.com.coletaflow.core.result.Result
import br.com.coletaflow.core.storage.TokenStorage
import br.com.coletaflow.data.remote.api.AuthApi
import br.com.coletaflow.domain.repositories.AuthRepository
import br.com.coletaflow.domain.repositories.AuthUser

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthUser> =
        try {
            val response = authApi.login(email, password)
            tokenStorage.saveToken(response.accessToken)
            Result.Success(
                AuthUser(
                    id = response.user.id,
                    name = response.user.name,
                    email = response.user.email,
                    role = response.user.role,
                ),
            )
        } catch (e: Exception) {
            Result.Error(e.message ?: "Erro ao fazer login")
        }

    override suspend fun logout() {
        tokenStorage.clearToken()
    }

    override suspend fun getStoredToken(): String? = tokenStorage.getToken()

    override suspend fun isLoggedIn(): Boolean = tokenStorage.getToken() != null
}
