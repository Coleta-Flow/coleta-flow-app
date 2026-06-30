package br.com.coletaflow

import android.app.Application
import br.com.coletaflow.di.androidModule
import br.com.coletaflow.di.appModule
import br.com.coletaflow.di.networkModule
import br.com.coletaflow.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ColetaFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ColetaFlowApplication)
            modules(
                androidModule,
                networkModule,
                repositoryModule,
                appModule,
            )
        }
    }
}
