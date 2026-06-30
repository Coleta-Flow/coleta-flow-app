package br.com.coletaflow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.coletaflow.domain.entities.Route
import br.com.coletaflow.domain.repositories.RouteRepository
import br.com.coletaflow.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoutesViewModel(
    private val routeRepository: RouteRepository,
) : ViewModel() {

    private val _routes = MutableStateFlow<UiState<List<Route>>>(UiState.Loading)
    val routes: StateFlow<UiState<List<Route>>> = _routes.asStateFlow()

    init {
        loadRoutes()
    }

    fun loadRoutes() {
        viewModelScope.launch {
            _routes.value = UiState.Loading
            routeRepository.getAssignedRoutes()
                .onSuccess { data -> _routes.value = UiState.Success(data) }
                .onError { msg, code -> _routes.value = UiState.Error(msg, code) }
        }
    }
}
