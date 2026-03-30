package com.nhom.smartparking.di

import com.nhom.smartparking.data.repository.AuthRepositoryImpl
import com.nhom.smartparking.data.repository.ParkingRepositoryImpl
import com.nhom.smartparking.domain.repository.AuthRepository
import com.nhom.smartparking.domain.repository.ParkingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindParkingRepository(impl: ParkingRepositoryImpl): ParkingRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}