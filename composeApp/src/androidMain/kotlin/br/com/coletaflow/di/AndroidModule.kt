package br.com.coletaflow.di

import br.com.coletaflow.core.storage.DataStoreTokenStorage
import br.com.coletaflow.core.storage.TokenStorage
import br.com.coletaflow.domain.repositories.LocationProvider
import br.com.coletaflow.location.AndroidLocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<TokenStorage> { DataStoreTokenStorage(androidContext()) }
    single<LocationProvider> { AndroidLocationProvider(androidContext()) }
}
