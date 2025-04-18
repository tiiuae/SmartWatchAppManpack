package com.tii.dcsmartwatchcode.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class HealthViewModel : ViewModel(),UpdateHealthDataValue {
    private val _heartRate = MutableLiveData<HealthDataClass>()
    val heartRate: LiveData<HealthDataClass> = _heartRate

    fun fetchHealthData() {
        viewModelScope.launch {
          /*  val heartRateValue = getHeartRateFromGoogleFit()  // Implement this function
            _heartRate.postValue(heartRateValue)*/
        }
    }

    override fun valueUpdated() : HealthDataClass {
        _heartRate.postValue(heartRateValue)
		return TODO("Provide the return value")
	}

    override fun error() {
        TODO("Not yet implemented")
    }
}
