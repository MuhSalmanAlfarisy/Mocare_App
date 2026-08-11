package com.mocare.app.di

import androidx.room.Room
import com.mocare.app.data.local.db.MocareDatabase
import com.mocare.app.data.repository.FuelRepository
import com.mocare.app.ui.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            MocareDatabase::class.java,
            "mocare_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    single { get<MocareDatabase>().fuelRecordDao() }
    single { FuelRepository(get()) }
    viewModel { HomeViewModel(get()) }
}
