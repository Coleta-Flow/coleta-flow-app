# coleta-flow-app

App do motorista da plataforma ColetaFlow.

**Stack:** Kotlin Multiplatform · Compose Multiplatform · Ktor · Coroutines · Koin · DataStore

---

## Quick start (Android)

1. Abrir no Android Studio Koala ou superior
2. Criar `local.properties` na raiz:
   ```
   sdk.dir=/path/to/android/sdk
   ```
3. Conferir `API_URL` em `composeApp/build.gradle.kts`
   - Emulador: `http://10.0.2.2:3333`
   - Device físico: IP da máquina na rede local
4. Rodar no emulador Android (API 26+)

---

## Estrutura

```
composeApp/src/
  commonMain/
    core/
      network/        → HttpClientFactory (Ktor + Bearer auth)
      storage/        → TokenStorage (interface)
      result/         → Result<T> sealed class
    data/
      auth/           → AuthRepositoryImpl (Ktor + TokenStorage)
      route/          → RouteRepositoryImpl (Ktor + DTO mapper)
      local/          → OfflineQueue (interface) + InMemoryOfflineQueue
      remote/
        api/          → AuthApi, RoutesApi
        dto/          → DTOs de request/response
      repositories/   → SyncManager
    domain/
      entities/       → Route, DriverLocation, CollectionPoint, RouteStop
      repositories/   → AuthRepository, RouteRepository, LocationProvider
      usecases/       → ValidateGeofenceUseCase (Haversine 100m)
    di/               → AppModule (Koin — networkModule, repositoryModule, appModule)
    features/
      auth/           → LoginScreen, LoginViewModel
      routes/         → RoutesListScreen, RoutesViewModel
      tracking/       → ActiveRouteScreen
      delivery/       → ConfirmDeliveryScreen
    presentation/
      viewmodels/     → ActiveRouteViewModel, RoutesViewModel
      navigation/     → Screen (sealed class)
      state/          → UiState<T>
      theme/          → ColetaFlowTheme, Colors
  androidMain/
    di/               → AndroidModule (TokenStorage + LocationProvider)
    core/storage/     → DataStoreTokenStorage
    location/         → AndroidLocationProvider (FusedLocationProviderClient)
    background/       → RouteTrackingService (Foreground Service)
  iosMain/
    location/         → IosLocationProvider (stub)
```

---

## Injeção de dependências (Koin)

```kotlin
// Módulo de plataforma (androidMain)
androidModule:
  TokenStorage → DataStoreTokenStorage(context)
  LocationProvider → AndroidLocationProvider(context)

// commonMain
networkModule:
  HttpClient → HttpClientFactory.create(tokenProvider = tokenStorage::getToken)
  RoutesApi, AuthApi

repositoryModule:
  AuthRepository → AuthRepositoryImpl(authApi, tokenStorage)
  RouteRepository → RouteRepositoryImpl(routesApi)
  OfflineQueue → InMemoryOfflineQueue()
  SyncManager(offlineQueue, routesApi)

appModule:
  ValidateGeofenceUseCase()
  LoginViewModel(authRepository)
  RoutesViewModel(routeRepository)
  ActiveRouteViewModel(routeId via params, routeRepository, locationProvider, validateGeofence)
```

---

## Fluxo do motorista

```
1. Login → JWT salvo no DataStore
2. RoutesListScreen → GET /routes/assigned
3. Seleciona rota (status PLANNED ou ASSIGNED)
4. ActiveRouteScreen:
   ├── Status PLANNED/ASSIGNED → botão "Iniciar Rota"
   │   └── PATCH /routes/:id/start → status vira IN_PROGRESS
   │       tracking token retornado (link para solicitante)
   └── Status IN_PROGRESS em diante → botão "Confirmar chegada"
       └── valida geofence → confirma no API

5. RouteTrackingService (Foreground Service):
   - GPS via FusedLocationProviderClient a cada 4s
   - POST /routes/:id/location { lat, lng, accuracy, speed, heading, battery }
   - Notificação persistente "Rota em andamento"
```

---

## Foreground Service (tracking GPS)

`RouteTrackingService` mantém envio de localização em background:
- `FusedLocationProviderClient` com intervalo de 4s / min 2s
- Inclui `battery` level via `BatteryManager`
- Notificação persistente com ação "Parar"
- Chama `routeRepository.sendLocation()` a cada leitura

---

## Geofence

`ValidateGeofenceUseCase` usa fórmula Haversine (raio padrão: 100m).
- Motorista vê feedback visual ao se aproximar do ponto
- A API também valida do lado do servidor

---

## Offline Queue

`OfflineQueue` armazena eventos pendentes quando sem internet:
- `LocationSnapshot` — posição GPS
- `RouteStarted` — início de rota
- `DriverArrived` — chegada no doador
- `MaterialCollected` — material coletado
- `DeliveryConfirmed` — entrega confirmada
- `WeightRegistered` — peso registrado

Quando internet volta, `SyncManager.syncPendingEvents()` processa a fila via `dequeueAll()`.

Implementação atual: `InMemoryOfflineQueue` (volátil). Para persistência real, implementar com SQLDelight ou DataStore.

---

## Permissões Android

```xml
ACCESS_FINE_LOCATION          <!-- localização precisa -->
ACCESS_BACKGROUND_LOCATION    <!-- localização em background -->
FOREGROUND_SERVICE            <!-- serviço persistente -->
FOREGROUND_SERVICE_LOCATION   <!-- tipo de foreground service (API 34+) -->
POST_NOTIFICATIONS            <!-- notificação persistente (API 33+) -->
```

---

## Testes

```bash
./gradlew :composeApp:testDebugUnitTest
```
