package com.mocare.app

import android.app.Application
import com.mocare.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MocareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MocareApplication)
            modules(listOf(appModule))
        }
    }
}