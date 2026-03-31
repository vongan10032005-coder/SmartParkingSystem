package com.nhom.smartparking.di

import android.content.Context
import androidx.room.Room
import com.nhom.smartparking.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart_parking.db"
        ).build()

    @Provides
    @Singleton
    fun provideVehicleRecordDao(db: AppDatabase) = db.vehicleRecordDao()
}