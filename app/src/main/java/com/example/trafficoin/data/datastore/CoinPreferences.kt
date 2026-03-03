package com.example.trafficoin.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


object CoinPreferenceKeys {
    val CURRENCY = stringPreferencesKey("coin_currency")
    val AMOUNT = intPreferencesKey("coin_amount")
}

data class CoinPreferences(
    val currency: String,
    val amount: Int
)

interface CoinPreferenceRepository {
    val preferencesFlow: Flow<CoinPreferences>
    suspend fun savUserInput(currency: String, amount: Int)
    suspend fun reset()
}

class CoinPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : CoinPreferenceRepository {
    override val preferencesFlow: Flow<CoinPreferences> =
        dataStore.data.map { prefs ->
            CoinPreferences(
                currency = prefs[CoinPreferenceKeys.CURRENCY] ?: "USD",
                amount = prefs[CoinPreferenceKeys.AMOUNT] ?: 1,
            )
        }

    override suspend fun savUserInput(currency: String, amount: Int) {
        dataStore.edit { prefs ->
            prefs[CoinPreferenceKeys.CURRENCY] = currency
            prefs[CoinPreferenceKeys.AMOUNT] = amount
        }
    }

    override suspend fun reset() {
        dataStore.edit { prefs ->
            prefs.remove(CoinPreferenceKeys.CURRENCY)
            prefs.remove(CoinPreferenceKeys.AMOUNT)
        }
    }
}