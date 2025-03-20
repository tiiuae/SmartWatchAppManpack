package com.tii.dcsmartwatchcode.presentation

import android.util.Log
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseUpdate
import kotlin.math.abs


class HealthMonitorUpdateCallback : ExerciseUpdateCallback {
	var bleServer: BLEServer? = null

	override fun onRegistered() {
		//TODO("Not yet implemented")
	}

	override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
		//TODO("Not yet implemented")
	}

	override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
		LogInfo("onExerciseUpdateReceived called")
		if(bleServer != null) {
			var data = ""
			for(i in update.latestMetrics.cumulativeDataPoints) {
				try {
					data = when(i.dataType.name) {
						"Steps" -> {
							i.total.toString()
						}
						else -> {
							String.format("%.2f", i.total as Double)
						}
					}
					//SendData(i.dataType.name, data)
					bleServer?.healthData?.set(i.dataType.name, data)
					LogInfo(i.dataType.name + "1 -- " +data)
				} catch(_e: Exception) {
					LogError("onExerciseUpdateReceived: ${i.dataType.name} ${_e.message}")
				}
			}
			for(i in update.latestMetrics.sampleDataPoints) {
				try {
					data = when(i.dataType.name) {
						"Step per minute" -> {
							i.value.toString()
						}
						"Absolute Elevation" -> {
							String.format("%.2f", abs(i.value as Double))
						}
						else -> {
							String.format("%.2f", i.value as Double)
						}
					}
					LogInfo(i.dataType.name + "2 -- " +data)
					//SendData(i.dataType.name, data)
					bleServer?.healthData?.set(i.dataType.name, data)
				} catch(_e: Exception) {
					LogError("onExerciseUpdateReceived: ${i.dataType.name} ${_e.message}")
				}
			}
			for(i in update.latestMetrics.statisticalDataPoints) {
				try {
					//SendData(i.dataType.name, String.format("%.2f", i.average))
					bleServer?.healthData?.set(i.dataType.name, String.format("%.2f", i.average))
					LogInfo(i.dataType.name + "3 -- " + String.format("%.2f", i.average))
				} catch(_e: Exception) {
					LogError("onExerciseUpdateReceived: ${i.dataType.name} ${_e.message}")
				}
			}
		}
	}

	override fun onRegistrationFailed(throwable: Throwable) {
		//TODO("Not yet implemented")
	}

	override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: Availability) {
		//TODO("Not yet implemented")
	}


	private fun LogInfo(_message: String) {
		Log.i("BLE Update", _message)
	}

	private fun LogError(_message: String) {
		Log.e("BLE", _message)
	}

	fun setBLE(bleServer: BLEServer) {
		this.bleServer = bleServer

	}
}