package com.paralearn.android.di

import com.paralearn.android.data.network.*
import com.paralearn.android.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        sessionManager: SessionManager,
        sessionCookieJar: SessionCookieJar
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(sessionCookieJar)
            .addInterceptor { chain ->
                val token = sessionManager.authToken.value
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://paralearn.duckdns.org")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserAPI(retrofit: Retrofit): UserAPI {
        return retrofit.create(UserAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideAlgorithmAPI(retrofit: Retrofit): AlgorithmAPI {
        return retrofit.create(AlgorithmAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideConfigurationAPI(retrofit: Retrofit): ConfigurationAPI {
        return retrofit.create(ConfigurationAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideCourseAPI(retrofit: Retrofit): CourseAPI {
        return retrofit.create(CourseAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideLanguageAPI(retrofit: Retrofit): LanguageAPI {
        return retrofit.create(LanguageAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMatriculateAPI(retrofit: Retrofit): MatriculateAPI {
        return retrofit.create(MatriculateAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideProgressAPI(retrofit: Retrofit): ProgressAPI {
        return retrofit.create(ProgressAPI::class.java)
    }
}
