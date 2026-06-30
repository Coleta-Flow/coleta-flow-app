# Setup — coleta-flow-app

## Pré-requisitos

- Android Studio Koala (2024.1) ou superior
- JDK 17
- Android SDK API 26+ (Android 8.0)
- Conta Mapbox (token de download)
- `coleta-flow-api` rodando

---

## Configuração inicial

### 1. Criar `local.properties`

```properties
# Caminho do Android SDK (ajuste para sua máquina)
sdk.dir=/Users/SEU_USUARIO/Library/Android/sdk

# Token para baixar o Mapbox Android SDK
# Obtenha em: https://account.mapbox.com → Tokens → Downloads
MAPBOX_DOWNLOADS_TOKEN=sk.eyJ1IjoiY29sZXRhZmxvdyIsImEi...
```

> `local.properties` está no `.gitignore` — nunca commite esse arquivo.

### 2. Configurar URL da API

Em `composeApp/build.gradle.kts`, o campo `API_URL` aponta para:
- **Emulador:** `http://10.0.2.2:3333` (loopback do host)
- **Device físico:** IP da máquina na rede local, ex: `http://192.168.1.100:3333`
- **Produção:** URL pública da API

```kotlin
buildConfigField("String", "API_URL", "\"http://10.0.2.2:3333\"")
```

### 3. Abrir no Android Studio

Abrir a pasta `coleta-flow-app/` como projeto.

### 4. Rodar no emulador ou device

Selecionar configuração `composeApp` → Run (⌘R).

---

## Build via Gradle (linha de comando)

```bash
# Debug APK
./gradlew :composeApp:assembleDebug

# Instalar no device conectado
./gradlew :composeApp:installDebug

# Unit tests
./gradlew :composeApp:testDebugUnitTest

# Release APK (requer keystore configurado)
./gradlew :composeApp:assembleRelease
```

---

## Secrets no CI (GitHub Actions)

Configure os seguintes secrets no repositório:

| Secret | Descrição |
|---|---|
| `MAPBOX_DOWNLOADS_TOKEN` | Token de download do Mapbox SDK |
| `PROD_API_URL` | URL da API em produção |
| `KEYSTORE_BASE64` | Keystore em Base64 (para release) |
| `KEYSTORE_PASSWORD` | Senha da keystore |
| `KEY_ALIAS` | Alias da chave |
| `KEY_PASSWORD` | Senha da chave |

---

## Estrutura de módulos Gradle

```
coleta-flow-app/
  composeApp/           → módulo principal (KMP + Compose)
  gradle/
    libs.versions.toml  → catálogo de versões (BOM)
  build.gradle.kts      → raiz do projeto
  settings.gradle.kts   → configuração Maven (Mapbox repo)
  gradle.properties     → flags de performance do Gradle
```

---

## Fluxo completo do motorista

```
1. Login (email + senha)
2. Lista de rotas atribuídas
3. Tap na rota → iniciar rota
   → API gera tracking token
   → Foreground Service inicia (GPS a cada 4s)
4. Navegar até o solicitante
   → GPS enviado via WebSocket
5. Confirmar chegada
6. Confirmar coleta do material
7. Navegar até o ponto de coleta
   → Geofence valida proximidade (100m)
8. Confirmar entrega no ponto
9. Registrar peso (balança)
10. Finalizar rota
    → Token invalidado
    → Foreground Service encerrado
```
