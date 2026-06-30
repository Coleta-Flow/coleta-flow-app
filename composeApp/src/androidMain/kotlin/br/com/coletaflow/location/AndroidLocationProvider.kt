package br.com.coletaflow.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import br.com.coletaflow.domain.entities.DriverLocation
import br.com.coletaflow.domain.repositories.LocationProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidLocationProvider(private val context: Context) : LocationProvider {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    override fun observeLocation(): Flow<DriverLocation> = callbackFlow {
        if (!hasPermission()) {
            close()
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(
                        DriverLocation(
                            lat = loc.latitude,
                            lng = loc.longitude,
                            speed = if (loc.hasSpeed()) loc.speed.toDouble() else null,
                            heading = if (loc.hasBearing()) loc.bearing.toInt() else null,
                            accuracy = if (loc.hasAccuracy()) loc.accuracy.toDouble() else null,
                            timestamp = java.time.Instant.now().toString(),
                        ),
                    )
                }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, context.mainLooper)

        awaitClose { fusedClient.removeLocationUpdates(callback) }
    }

    override suspend fun getCurrentLocation(): DriverLocation = suspendCancellableCoroutine { cont ->
        if (!hasPermission()) {
            cont.cancel()
            return@suspendCancellableCoroutine
        }

        fusedClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                cont.resume(
                    DriverLocation(
                        lat = loc.latitude,
                        lng = loc.longitude,
                        timestamp = java.time.Instant.now().toString(),
                    ),
                )
            } else {
                cont.cancel()
            }
        }
    }

    override fun isGpsEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }
}
