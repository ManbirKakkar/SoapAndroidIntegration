package com.asml.pdscanner.network.soap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asml.pdscanner.utils.ResultValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SoapViewModel : ViewModel() {
    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun calculateSum(a: Int, b: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _result.value = ""
            when (val result = SoapHelper.addNumbers(a, b)) {
                is ResultValue.Success -> _result.value = "$a + $b = ${result.value}"
                is ResultValue.Failure -> _result.value = "Error: ${result.exception.message}"
            }
            _isLoading.value = false
        }
    }
}