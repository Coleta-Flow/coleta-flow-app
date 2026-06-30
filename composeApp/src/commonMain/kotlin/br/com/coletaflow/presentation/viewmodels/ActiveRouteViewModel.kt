package br.com.coletaflow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.coletaflow.domain.entities.CollectionPoint
import br.com.coletaflow.domain.entities.DriverLocation
import br.com.coletaflow.domain.entities.Route
import br.com.coletaflow.domain.repositories.LocationProvider
import br.com.coletaflow.domain.repositories.RouteRepository
import br.com.coletaflow.domain.usecases.GeofenceResult
import br.com.coletaflow.domain.usecases.ValidateGeofenceUseCase
import br.com.coletaflow.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ActiveRouteViewModel(
    private val routeId: String,
    private val routeRepository: RouteRepository,
    private val locationProvider: LocationProvider,
    private val validateGeofence: ValidateGeofenceUseCase,
) : ViewModel() {

    private val _route = MutableStateFlow<UiState<Route>>(UiState.Loading)
    val route: StateFlow<UiState<Route>> = _route.asStateFlow()

    private val _currentLocation = MutableStateFlow<DriverLocation?>(null)
    val currentLocation: StateFlow<DriverLocation?> = _currentLocation.asStateFlow()

    private val _geofenceResult = MutableStateFlow<GeofenceResult?>(null)
    val geofenceResult: StateFlow<GeofenceResult?> = _geofenceResult.asStateFlow()

    private val _actionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val actionState: StateFlow<UiState<Unit>> = _actionState.asStateFlow()

    init {
        loadRoute()
        startObservingLocation()
    }

    private fun loadRoute() {
        viewModelScope.launch {
            routeRepository.getRouteById(routeId)
                .onSuccess { data -> _route.value = UiState.Success(data) }
                .onError { msg, code -> _route.value = UiState.Error(msg, code) }
        }
    }

    private fun startObservingLocation() {
        viewModelScope.launch {
            locationProvider.observeLocation().collectLatest { location ->
                _currentLocation.value = location
                routeRepository.sendLocation(routeId, location)
            }
        }
    }

    fun validateDeliveryGeofence(collectionPoint: CollectionPoint) {
        val location = _currentLocation.value ?: return
        val result = validateGeofence(location.lat, location.lng, collectionPoint.lat, collectionPoint.lng)
        _geofenceResult.value = result
    }

    fun startRoute() {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            routeRepository.startRoute(routeId)
                .onSuccess {
                    _actionState.value = UiState.Idle
                    loadRoute()
                }
                .onError { msg, code -> _actionState.value = UiState.Error(msg, code) }
        }
    }

    fun confirmArrival() {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            routeRepository.confirmArrival(routeId)
                .onSuccess { _actionState.value = UiState.Success(Unit) }
                .onError { msg, code -> _actionState.value = UiState.Error(msg, code) }
        }
    }

    fun confirmCollection() {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            routeRepository.confirmCollection(routeId)
                .onSuccess { _actionState.value = UiState.Success(Unit) }
                .onError { msg, code -> _actionState.value = UiState.Error(msg, code) }
        }
    }
}
