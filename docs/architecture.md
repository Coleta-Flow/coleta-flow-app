# Arquitetura — coleta-flow-app (KMP)

**Stack:** Kotlin Multiplatform · Compose Multiplatform · Ktor · Coroutines · Koin · DataStore · Mapbox Android SDK

---

## Por que Kotlin Multiplatform?

- **Compartilhamento real de lógica** — domain, data, use cases e ViewModels vivem em `commonMain`
- **UI nativa por plataforma** — Compose Multiplatform no Android (SwiftUI no iOS quando necessário)
- **Foreground Service real** — `androidMain` acessa APIs nativas do Android sem bridge
- **Performance nativa** — sem JavaScript, sem bridge
- **Preparado para iOS** — stubs em `iosMain`, prontos para implementação futura

---

## Clean Architecture em camadas

```
Screen (Compose)
    ↓
ViewModel (presentation)
    ↓
UseCase (domain) ← não depende de nenhum framework
    ↓
Repository (interface)
    ↓
RepositoryImpl (data)
    ├── Ktor (REST + WebSocket)
    ├── DataStore (local)
    └── OfflineQueue
```

**Regra fundamental:** a camada `domain` contém apenas Kotlin puro — nenhum import de framework.

---

## Organização de sourceSets

```
composeApp/src/
  commonMain/       → código compartilhado (funciona em todas as plataformas)
    app/            → NavHost + composable routes
    core/           → Ktor client, DataStore, Result wrapper
    data/           → DTOs, APIs Ktor, RepositoryImpl, OfflineQueue, SyncManager
    di/             → módulos Koin (network, repository, app)
    domain/         → entidades, interfaces de repositório, use cases
    features/       → telas por domínio (auth, routes, tracking, delivery)
    presentation/   → ViewModels, UiState, tema Compose, navegação

  androidMain/      → implementações Android-específicas
    ColetaFlowApplication.kt    → Application class, inicializa Koin
    MainActivity.kt
    background/
      RouteTrackingService.kt   → Foreground Service (START_STICKY)
    location/
      AndroidLocationProvider.kt → FusedLocationProviderClient
    core/storage/
      DataStoreTokenStorage.kt  → persistência do JWT

  iosMain/          → stubs preparados para implementação futura
    location/IosLocationProvider.kt
```

---

## Foreground Service (Android)

Mantém envio de localização ativo com app em background ou tela desligada.

```kotlin
class RouteTrackingService : Service() {
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    startForeground(NOTIFICATION_ID, buildNotification())
    serviceScope.launch {
      while (isActive) {
        val location = locationProvider.getCurrentLocation()
        routesApi.sendLocation(routeId, location.toPayload())  // ou OfflineQueue se sem internet
        delay(4_000)
      }
    }
    return START_STICKY
  }
}
```

**Intervalo:** 4 segundos
**Notificação:** persistente, não descartável durante a rota
**Canal:** `coletaflow_tracking`

---

## OfflineQueue

Eventos armazenados localmente quando sem internet:

```kotlin
sealed class PendingEvent {
  data class LocationSnapshot(val latitude: Double, val longitude: Double, val timestamp: Long)
  data class DriverArrived(val stopId: String)
  data class MaterialCollected(val stopId: String)
  data class DeliveryConfirmed(val latitude: Double, val longitude: Double)
  data class WeightRegistered(val grossWeightKg: Double, val tareKg: Double?)
  data object RouteStarted
}
```

Quando internet volta, `SyncManager` processa a fila em ordem com os timestamps originais.

---

## Geofence (ValidateGeofenceUseCase)

```kotlin
class ValidateGeofenceUseCase(private val radiusMeters: Double = 100.0) {
  operator fun invoke(currentLat, currentLng, pointLat, pointLng): GeofenceResult

  sealed class GeofenceResult {
    data class WithinRange(val distanceMeters: Double)
    data class OutOfRange(val distanceMeters: Double, val requiredRadius: Double)
  }
}
```

Fórmula Haversine em `commonMain` — validada também no servidor.

---

## Injeção de dependências (Koin)

```kotlin
// di/AppModule.kt
val networkModule = module {
  single { HttpClientFactory.create(baseUrl = BuildConfig.API_URL) }
  single { RoutesApi(get()) }
  single { AuthApi(get()) }
}

val repositoryModule = module {
  // single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
  // single<RouteRepository> { RouteRepositoryImpl(get(), get()) }
  single { SyncManager(get(), get()) }
}

val appModule = module {
  viewModel { LoginViewModel(get()) }
  viewModel { RoutesViewModel(get()) }
  viewModel { params -> ActiveRouteViewModel(params.get(), get(), get(), get()) }
}
```

---

## UiState

```kotlin
sealed class UiState<out T> {
  data object Loading : UiState<Nothing>()
  data object Idle : UiState<Nothing>()
  data class Success<T>(val data: T) : UiState<T>()
  data class Error(val message: String, val code: String? = null) : UiState<Nothing>()
}
```

---

## Tema Compose (ColetaFlowTheme)

| Token | Cor | Uso |
|---|---|---|
| `EmeraldPrimary` | `#10B981` | Botões primários, ícones ativos |
| `EmeraldDark` | `#047857` | Hover, TopAppBar ícones |
| `EmeraldDeep` | `#064E3B` | Background de login, textos de ênfase |
| `MintLight` | `#D1FAE5` | Backgrounds de cards |
| `StatusDanger` | `#EF4444` | Erros, geofence fora do raio |
| `StatusWarning` | `#F59E0B` | Alertas de geofence |

---

## Navegação

```
Screen.Login → Screen.RoutesList
Screen.RoutesList → routes/{routeId}/active
routes/{routeId}/active → routes/{routeId}/weight
```

`Screen` é uma sealed class com `route: String` para cada destino.

---

## Permissões Android necessárias

| Permissão | Uso |
|---|---|
| `ACCESS_FINE_LOCATION` | GPS preciso |
| `ACCESS_BACKGROUND_LOCATION` | GPS com app em background |
| `FOREGROUND_SERVICE_LOCATION` | Tipo do foreground service |
| `POST_NOTIFICATIONS` | Notificação persistente (Android 13+) |
| `CAMERA` | Fotos dos materiais |
| `INTERNET` | Comunicação com API |
