# coleta-flow-app

App do motorista/coletor da plataforma ColetaFlow.

**Stack:** Kotlin Multiplatform · Compose Multiplatform · Ktor · Coroutines · Koin · DataStore

---

## Quick start (Android)

1. Abrir o projeto no Android Studio Koala ou superior
2. Criar `local.properties` na raiz com:
   ```
   sdk.dir=/path/to/android/sdk
   MAPBOX_DOWNLOADS_TOKEN=seu_token_aqui
   ```
3. Configurar `API_URL` em `composeApp/build.gradle.kts` (default: `http://10.0.2.2:3333` para emulador)
4. Rodar no emulador ou device Android (API 26+)

---

## Estrutura

```
composeApp/src/
  commonMain/         → código compartilhado (domain, data, presentation)
    app/              → entry point e navegação
    core/             → network (Ktor), storage, result wrapper
    data/             → repositórios, DTOs, offline queue
    domain/           → entidades, interfaces de repositório, use cases
    presentation/     → ViewModels, estados, tema, navegação
    features/         → telas por domínio (auth, routes, tracking, delivery)
  androidMain/        → implementações Android-específicas
    location/         → FusedLocationProviderClient
    background/       → Foreground Service para tracking em background
    map/              → Mapbox Android SDK
  iosMain/            → stubs iOS (prontos para implementação)
    location/         → stub CLLocationManager
    map/              → stub MapKit
```

---

## Arquitetura (Clean Architecture)

```
Screen (Compose) → ViewModel → UseCase → Repository (interface)
                                              ↓
                                   Repository Implementation
                                   ├── Ktor HTTP/WebSocket
                                   ├── DataStore (local)
                                   └── OfflineQueue
```

**Regra:** camada `domain` não depende de nenhum framework.

---

## Foreground Service

O `RouteTrackingService` mantém envio de localização ativo em background:
- Envia GPS a cada 4 segundos
- Exibe notificação persistente "Rota em andamento"
- Se sem internet: salva na `OfflineQueue`

---

## Geofence

`ValidateGeofenceUseCase` usa fórmula Haversine (100m padrão).
Motorista **não pode** confirmar entrega fora do raio do ponto cadastrado.
A API também valida do lado do servidor.

---

## Offline

`OfflineQueue` armazena eventos pendentes:
- `LocationSnapshot`, `RouteStarted`, `DriverArrived`
- `MaterialCollected`, `DeliveryConfirmed`, `WeightRegistered`

Quando internet voltar, `SyncManager` processa a fila em ordem.

---

## Permissões Android

- `ACCESS_FINE_LOCATION` — localização precisa
- `ACCESS_BACKGROUND_LOCATION` — localização em background
- `FOREGROUND_SERVICE` — serviço de foreground
- `FOREGROUND_SERVICE_LOCATION` — tipo de foreground service
- `POST_NOTIFICATIONS` — notificação persistente (Android 13+)

---

## Testes

```bash
./gradlew :composeApp:testDebugUnitTest
```
