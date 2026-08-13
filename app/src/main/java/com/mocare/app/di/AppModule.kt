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
        // ⚠️ JANGAN gunakan .fallbackToDestructiveMigration() lagi!
        // Mulai versi 6, setiap perubahan skema WAJIB menggunakan Migration
        // eksplisit di MocareDatabase.kt. Tanpa fallback destructive, Room
        // akan throw IllegalStateException jika ada versi baru tanpa
        // Migration — sehingga bug migrasi terdeteksi saat development,
        // bukan diam-diam menghapus data user di production.
        .build()
    }

    single { get<MocareDatabase>().fuelRecordDao() }
    single { FuelRepository(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { com.mocare.app.ui.viewmodel.HistoryViewModel(get()) }
    viewModel { com.mocare.app.ui.viewmodel.StatsViewModel(get()) }
}
