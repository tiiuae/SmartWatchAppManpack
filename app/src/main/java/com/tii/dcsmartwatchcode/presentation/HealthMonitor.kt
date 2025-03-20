package com.tii.dcsmartwatchcode.presentation

import android.util.Log
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.clearUpdateCallback
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseTrackedStatus
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.WarmUpConfig
import androidx.health.services.client.endExercise
import androidx.health.services.client.getCurrentExerciseInfo
import androidx.health.services.client.prepareExercise
import androidx.health.services.client.startExercise

class HealthMonitor(private val exerciseClient: ExerciseClient) {
    val dataTypes = setOf(
        DataType.CALORIES_TOTAL
        , DataType.DISTANCE_TOTAL
        // , DataType.LOCATION // discreet communicator already sends the location info to FMO
        , DataType.STEPS_TOTAL
        , DataType.HEART_RATE_BPM
        , DataType.FLOORS_TOTAL
        , DataType.ELEVATION_GAIN_TOTAL
        , DataType.ABSOLUTE_ELEVATION
        , DataType.PACE
        , DataType.SPEED
        , DataType.STEPS_PER_MINUTE
    )

    suspend fun Prepare(): Boolean {
        val warmUpConfig = WarmUpConfig(
            ExerciseType.RUNNING
            , setOf(
                DataType.HEART_RATE_BPM
                // , DataType.LOCATION
            )
        )
        try {
            exerciseClient.prepareExercise(warmUpConfig)
            LogInfo("prepareExercise set true")

            return true
        } catch(_e: Exception) {
            LogError("Failed to prepare health monitor: ${_e.message}")
        }
        return false
    }

    suspend fun SetUpdateCallback(_updateCallback: HealthMonitorUpdateCallback ,bleServer: BLEServer): Boolean {
        try {
            _updateCallback.setBLE(bleServer)
            exerciseClient.setUpdateCallback(_updateCallback)
            LogInfo("Callback set true")
            return true
        } catch(_e: Exception) {
            LogError("Failed to set update callback: ${_e.message}")
        }
        return false
    }

    suspend fun UnsetUpdateCallback(_updateCallback: HealthMonitorUpdateCallback): Boolean {
        try {
            exerciseClient.clearUpdateCallback(_updateCallback)
            return true
        } catch(_e: Exception) {
            LogError("Failed to unset update callback: ${_e.message}")
        }
        return false
    }

    suspend fun Start(): Boolean {
        val config = ExerciseConfig(
            ExerciseType.RUNNING
            , dataTypes
            , false
            , true
        )
        try {
            exerciseClient.startExercise(config)
            return true
        } catch(_e: Exception) {
            LogError("Failed to start health monitor: ${_e.message}")
        }
        return false
    }

    suspend fun Stop(): Boolean {
        try {
            exerciseClient.endExercise()
            return true
        } catch(_e: Exception) {
            LogError("Failed to stop health monitor: ${_e.message}")
        }
        return false
    }

    suspend fun IsRunning(): Boolean {
        val exerciseInfo = exerciseClient.getCurrentExerciseInfo()
        return (
            exerciseInfo.exerciseTrackedStatus != ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS
            && exerciseInfo.exerciseTrackedStatus != ExerciseTrackedStatus.UNKNOWN
        )
    }

    private fun LogInfo(_message: String) {
        Log.i("BLE", _message)
    }

    private fun LogError(_message: String) {
        Log.e("BLE", _message)
    }

}

