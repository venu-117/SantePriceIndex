package com.sante.priceindex.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sante.priceindex.data.model.MandiPrice
import com.sante.priceindex.data.model.TrendDataPoint
import com.sante.priceindex.data.repository.MandiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SharedViewModel : ViewModel() {

    private val repository = MandiRepository()

    private val _prices = MutableLiveData<List<MandiPrice>>()
    val prices: LiveData<List<MandiPrice>> = _prices

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedVegetable = MutableLiveData<MandiPrice?>()
    val selectedVegetable: LiveData<MandiPrice?> = _selectedVegetable

    private val _trendData = MutableLiveData<List<TrendDataPoint>>()
    val trendData: LiveData<List<TrendDataPoint>> = _trendData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Last refresh timestamp
    private val _lastRefreshed = MutableLiveData<String>()
    val lastRefreshed: LiveData<String> = _lastRefreshed

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                refreshPrices(isInitialLoad = _prices.value == null)
                delay(60000) // 1 minute delay
            }
        }
    }

    fun loadPrices() {
        refreshPrices(isInitialLoad = true)
    }

    private fun refreshPrices(isInitialLoad: Boolean) {
        viewModelScope.launch {
            if (isInitialLoad) _isLoading.value = true
            try {
                val data = repository.fetchTodayPrices()
                _prices.value = data
                _lastRefreshed.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                _errorMessage.value = null
            } catch (e: Exception) {
                if (isInitialLoad) {
                    _errorMessage.value = "Failed to load prices. Using cached data."
                    _prices.value = MandiRepository.MOCK_PRICES
                }
            } finally {
                if (isInitialLoad) _isLoading.value = false
            }
        }
    }

    fun selectVegetable(item: MandiPrice) {
        _selectedVegetable.value = item
        loadTrendData(item.id)
    }

    fun loadTrendData(vegetableId: String) {
        viewModelScope.launch {
            try {
                val data = repository.fetchTrendData(vegetableId)
                _trendData.value = data
            } catch (e: Exception) {
                _errorMessage.value = "Could not load trend data."
            }
        }
    }

    fun clearSelection() {
        _selectedVegetable.value = null
    }
}
