package com.example.trafficoin.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import java.io.File

/**
 * @author Jack
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoinPreferenceRepositoryTest {
    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { File.createTempFile("test_prefs", ".preferences_pb") }
    )

    private lateinit var repository: CoinPreferencesRepositoryImpl

    @Before
    fun setup() {
        repository = CoinPreferencesRepositoryImpl(testDataStore)
    }

    @After
    fun tearDown() {
        val testFile = File("test_prefs.preferences_pb")
        if (testFile.exists()) testFile.delete()
    }

    @Test
    fun `preferencesFlow - should emit default values`() = runTest {
        val initialPrefs = repository.preferencesFlow.first()

        assert(initialPrefs.currency == "USD")
        assert(initialPrefs.amount == 1)
    }

    @Test
    fun `saveUserInput - should update flow with new values`() = runTest {
        val newCurrency = "JPY"
        val newAmount = 3000
        repository.savUserInput(newCurrency, newAmount)

        val updatedPrefs = repository.preferencesFlow.first()
        assert(updatedPrefs.currency == newCurrency)
        assert(updatedPrefs.amount == newAmount)
    }

    @Test
    fun `reset - should restore flow to default values`() = runTest {
        repository.savUserInput("JPY", 3000)
        repository.reset()

        val resetPrefs = repository.preferencesFlow.first()
        assert(resetPrefs.currency == "USD")
        assert(resetPrefs.amount == 1)
    }
}