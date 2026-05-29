package com.paralearn.android.di

import com.paralearn.android.data.repository.*
import com.paralearn.android.domain.repository.*
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
    abstract fun bindUserRepository(
        userRepositoryImpl: HybridUserRepository
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAlgorithmRepository(
        algorithmRepositoryImpl: HybridAlgorithmRepository
    ): AlgorithmRepository

    @Binds
    @Singleton
    abstract fun bindConfigurationRepository(
        configurationRepositoryImpl: HybridConfigurationRepository
    ): ConfigurationRepository

    @Binds
    @Singleton
    abstract fun bindCourseRepository(
        courseRepositoryImpl: HybridCourseRepository
    ): CourseRepository

    @Binds
    @Singleton
    abstract fun bindLessonRepository(
        lessonRepositoryImpl: HybridLessonRepository
    ): LessonRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        languageRepositoryImpl: HybridLanguageRepository
    ): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindMatriculateRepository(
        matriculateRepositoryImpl: HybridMatriculateRepository
    ): MatriculateRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        progressRepositoryImpl: HybridProgressRepository
    ): ProgressRepository
}
