package co.edu.uniquindio.demoapp.di

import co.edu.uniquindio.demoapp.data.repository.memory.ReportRepositoryImpl
import co.edu.uniquindio.demoapp.data.repository.memory.UserRepositoryImpl
import co.edu.uniquindio.demoapp.domain.repository.ReportRepository
import co.edu.uniquindio.demoapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import co.edu.uniquindio.demoapp.data.repository.local.SessionRepositoryImpl
import co.edu.uniquindio.demoapp.domain.repository.SessionRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds // Indica a Hilt que esta función vincula una implementación a una interfaz
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository // Vincula ReportRepositoryImpl con ReportRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository // Vincula UserRepositoryImpl con UserRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
}