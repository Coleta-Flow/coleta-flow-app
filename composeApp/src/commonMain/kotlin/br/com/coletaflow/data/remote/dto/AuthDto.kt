package br.com.coletaflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
)
