package br.com.coletaflow.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.coletaflow.core.result.Result
import br.com.coletaflow.domain.repositories.AuthRepository
import br.com.coletaflow.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = UiState.Error("Preencha e-mail e senha")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> _uiState.value = UiState.Success(Unit)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }
}
