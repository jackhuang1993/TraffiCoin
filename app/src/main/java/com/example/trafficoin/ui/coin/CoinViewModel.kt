package com.example.trafficoin.ui.coin

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trafficoin.core.util.*
import com.example.trafficoin.data.coin.CoinRepository
import com.example.trafficoin.data.coin.ExchangeRate
import com.example.trafficoin.data.datastore.CoinPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

data class CoinUiState(
    override val data: List<ExchangeItem> = emptyList(),
    override val lastUpdate: String = "--:--:--",
    override val isLoading: Boolean = false,
    override val errorMsg: String? = null,
    val baseCurrency: String? = null
) : RefreshableUiState<ExchangeItem>

@Parcelize
data class ExchangeItem(val code: String, val amount: String) : Parcelable

/**
 * @author Jack
 */
@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepo: CoinRepository,
    private val prefsRepo: CoinPreferenceRepository
) : ViewModel() {
    private var lastData = emptyList<ExchangeItem>()
    private var lastTime = "--:--:--"

    private val ticker = AutoRefreshTicker()

    private val apiDataFlow = ticker.flowWithTicker {
        coinRepo.getRates().also { lastTime = Utils.getCurrentDate() }
    }

    val uiState: StateFlow<CoinUiState> =
        combine(apiDataFlow, prefsRepo.preferencesFlow) { rates, prefs ->
            val items = calculateExchangeResults(rates, prefs.currency, prefs.amount.toString())
            lastData = items
                .filter { it.code in TARGET_CURRENCIES }
                .sortedBy { TARGET_CURRENCIES.indexOf(it.code) }
            CoinUiState(
                lastData, lastTime,
                baseCurrency = prefs.currency
            )
        }.catch { e ->
            emit(CoinUiState(lastData, lastTime, errorMsg = e.toUiStateErrorMessage()))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinUiState(isLoading = true)
        )

    fun manualRefresh() {
        ticker.trigger()
    }

    fun onInputChanged(currency: String, amount: String) {
        viewModelScope.launch {
            prefsRepo.savUserInput(currency, amount.toInt())
        }
    }

    fun onReset() {
        viewModelScope.launch { prefsRepo.reset() }
    }

    private fun calculateExchangeResults(
        rates: List<ExchangeRate>,
        fromCurrency: String,
        amount: String
    ): List<ExchangeItem> {
        val fromRate = rates.firstOrNull { it.currency == fromCurrency }?.rate?.toBigDecimal()
        if (fromRate == null) {
            return rates.map {
                ExchangeItem(it.currency, it.rate.toBigDecimal().toScaleString())
            }
        }

        val inputNum = amount.toBigDecimal()

        return rates.map { rate ->
            val toRate = rate.rate.toBigDecimal()

            val converted = inputNum
                .divide(fromRate, 10, RoundingMode.HALF_UP)
                .multiply(toRate)
                .toScaleString()

            ExchangeItem(rate.currency, converted)
        }
    }

    private fun BigDecimal.toScaleString(scale: Int = 3): String {
        return setScale(scale, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }

    companion object {
        private val TARGET_CURRENCIES = listOf("USD", "EUR", "CNY", "HKD", "JPY", "GBP")
    }
}