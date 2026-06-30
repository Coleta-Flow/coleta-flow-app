package br.com.coletaflow.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import br.com.coletaflow.domain.entities.DriverLocation
import br.com.coletaflow.domain.repositories.LocationProvider
import br.com.coletaflow.domain.repositories.RouteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RouteTrackingService : Service() {

    private val locationProvider: LocationProvider by inject()
    private val routeRepository: RouteRepository by inject()

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var routeId: String? = null

    companion object {
        const val EXTRA_ROUTE_ID = "extra_route_id"
        const val ACTION_STOP = "action_stop_tracking"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "coletaflow_tracking"
        private const val LOCATION_INTERVAL_MS = 4_000L
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        routeId = intent?.getStringExtra(EXTRA_ROUTE_ID)
        startForeground(NOTIFICATION_ID, buildNotification())

        serviceScope.launch {
            while (isActive) {
                try {
                    val location = locationProvider.getCurrentLocation()
                    val battery = getBatteryLevel()
                    val locationWithBattery = location.copy(battery = battery)
                    routeId?.let { id -> routeRepository.sendLocation(id, locationWithBattery) }
                } catch (_: Exception) { }
                delay(LOCATION_INTERVAL_MS)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun getBatteryLevel(): Int? {
        val batteryStatus = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: return null
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null
        return (level * 100 / scale.toFloat()).toInt()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Rastreamento de Rota",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Notificação ativa durante coletas em andamento"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, RouteTrackingService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ColetaFlow")
            .setContentText("Rota em andamento")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Parar", stopPendingIntent)
            .build()
    }
}
