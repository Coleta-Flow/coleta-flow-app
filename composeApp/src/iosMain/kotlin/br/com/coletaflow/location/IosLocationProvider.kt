package br.com.coletaflow.location

import br.com.coletaflow.domain.entities.DriverLocation
import br.com.coletaflow.domain.repositories.LocationProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// TODO: Implementar com CLLocationManager quando iOS for necessário
class IosLocationProvider : LocationProvider {
    override fun observeLocation(): Flow<DriverLocation> = emptyFlow()

    override suspend fun getCurrentLocation(): DriverLocation {
        throw NotImplementedError("iOS location not implemented yet")
    }

    override fun isGpsEnabled(): Boolean = false

    override fun hasPermission(): Boolean = false
}
