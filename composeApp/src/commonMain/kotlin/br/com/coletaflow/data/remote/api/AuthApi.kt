package br.com.coletaflow.data.remote.api

import br.com.coletaflow.data.remote.dto.LoginRequestDto
import br.com.coletaflow.data.remote.dto.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val client: HttpClient) {

    suspend fun login(email: String, password: String): LoginResponseDto =
        client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(email, password))
        }.body()
}
