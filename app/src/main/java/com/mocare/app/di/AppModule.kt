package com.mocare.app.di

import androidx.room.Room
import com.mocare.app.data.local.db.MocareDatabase
import com.mocare.app.data.local.dao.MotorDao
import com.mocare.app.data.local.dao.FuelRecordDao
import com.mocare.app.data.repository.MotorRepository
import com.mocare.app.data.repository.FuelRecordRepository
import com.mocare.app.ui.viewmodel.AddMotorViewModel
import com.mocare.app.ui.viewmodel.FuelInputViewModel
import com.mocare.app.ui.viewmodel.HomeViewModel
import com.mocare.app.ui.viewmodel.SummaryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Room Database
    single {
        Room.databaseBuilder(
            androidApplication(),
            MocareDatabase::class.java,
            "mocare_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    // DAOs
    single<MotorDao> { get<MocareDatabase>().motorDao() }
    single<FuelRecordDao> { get<MocareDatabase>().fuelRecordDao() }

    // Repositories
    single { MotorRepository(get()) }
    single { FuelRecordRepository(get()) }

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { AddMotorViewModel(get()) }
    viewModel { FuelInputViewModel(get()) }
    viewModel { SummaryViewModel(get(), get()) }
}
