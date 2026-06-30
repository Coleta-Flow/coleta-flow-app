package br.com.coletaflow.presentation.state

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data object Idle : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val code: String? = null) : UiState<Nothing>()
}

fun <T> UiState<T>.isLoading() = this is UiState.Loading
fun <T> UiState<T>.isSuccess() = this is UiState.Success
fun <T> UiState<T>.isError() = this is UiState.Error
fun <T> UiState<T>.dataOrNull() = (this as? UiState.Success)?.data
