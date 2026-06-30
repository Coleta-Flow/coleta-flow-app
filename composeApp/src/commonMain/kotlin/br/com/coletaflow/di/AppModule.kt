package br.com.coletaflow.di

import br.com.coletaflow.core.network.HttpClientFactory
import br.com.coletaflow.core.storage.TokenStorage
import br.com.coletaflow.data.auth.AuthRepositoryImpl
import br.com.coletaflow.data.remote.api.AuthApi
import br.com.coletaflow.data.remote.api.RoutesApi
import br.com.coletaflow.data.local.InMemoryOfflineQueue
import br.com.coletaflow.data.local.OfflineQueue
import br.com.coletaflow.data.repositories.SyncManager
import br.com.coletaflow.data.route.RouteRepositoryImpl
import br.com.coletaflow.domain.repositories.AuthRepository
import br.com.coletaflow.domain.repositories.RouteRepository
import br.com.coletaflow.domain.usecases.ValidateGeofenceUseCase
import br.com.coletaflow.features.auth.LoginViewModel
import br.com.coletaflow.presentation.viewmodels.ActiveRouteViewModel
import br.com.coletaflow.presentation.viewmodels.RoutesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single {
        val tokenStorage = get<TokenStorage>()
        HttpClientFactory.create(tokenProvider = { tokenStorage.getToken() })
    }
    single { RoutesApi(get()) }
    single { AuthApi(get()) }
}

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<RouteRepository> { RouteRepositoryImpl(get()) }
    single<OfflineQueue> { InMemoryOfflineQueue() }
    single { SyncManager(get(), get()) }
}

val appModule = module {
    single { ValidateGeofenceUseCase() }
    viewModel { LoginViewModel(get()) }
    viewModel { RoutesViewModel(get()) }
    viewModel { params -> ActiveRouteViewModel(params.get(), get(), get(), get()) }
}
