package br.com.coletaflow.core.network

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(tokenProvider: suspend () -> String?): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenProvider() ?: return@loadTokens null
                        BearerTokens(accessToken = token, refreshToken = "")
                    }
                }
            }

            install(WebSockets)

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
        }
    }
}
