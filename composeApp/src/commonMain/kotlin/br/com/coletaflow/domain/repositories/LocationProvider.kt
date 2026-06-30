package br.com.coletaflow.domain.repositories

import br.com.coletaflow.domain.entities.DriverLocation
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun observeLocation(): Flow<DriverLocation>
    suspend fun getCurrentLocation(): DriverLocation
    fun isGpsEnabled(): Boolean
    fun hasPermission(): Boolean
}
