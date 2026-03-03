package com.example.trafficoin.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.trafficoin.data.coin.CoinRepository
import com.example.trafficoin.data.coin.CoinRepositoryImp
import com.example.trafficoin.data.datastore.CoinPreferenceRepository
import com.example.trafficoin.data.datastore.CoinPreferencesRepositoryImpl
import com.example.trafficoin.data.flight.FlightRepository
import com.example.trafficoin.data.flight.FlightRepositoryImp
import com.example.trafficoin.manager.media.ImageDownloader
import com.example.trafficoin.network.coin.CoinApi
import com.example.trafficoin.network.flight.FlightApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author Jack
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val PREFS_NAME = "my_preferences"

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(PREFS_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideCoinPreferenceRepository(dataStore: DataStore<Preferences>): CoinPreferenceRepository {
        return CoinPreferencesRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideFlightRepository(api: FlightApi): FlightRepository {
        return FlightRepositoryImp(api)
    }

    @Provides
    @Singleton
    fun provideCoinRepository(api: CoinApi): CoinRepository {
        return CoinRepositoryImp(api)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(client: OkHttpClient): Retrofit.Builder {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
        }
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
    }

    @Provides
    @Singleton
    fun provideFlightApi(builder: Retrofit.Builder): FlightApi {
        return builder.baseUrl("https://www.kia.gov.tw/").build()
            .create(FlightApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCoinApi(builder: Retrofit.Builder): CoinApi {
        return builder.baseUrl("https://api.freecurrencyapi.com/").build()
            .create(CoinApi::class.java)
    }


    @Provides
    @Singleton
    fun provideImageDownloader(
        @ApplicationContext context: Context,
        client: OkHttpClient
    ): ImageDownloader {
        return ImageDownloader(context, client)
    }

}