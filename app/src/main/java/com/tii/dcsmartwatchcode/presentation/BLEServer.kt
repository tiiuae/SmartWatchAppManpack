package com.tii.dcsmartwatchcode.presentation

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.health.services.client.HealthServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class  BLEServer(private val context: Context) {

	private val healthMonitorUpdateCallback = HealthMonitorUpdateCallback()
	var count = 0
	private val SERVICE_UUID: UUID = UUID.fromString("00006969-0000-1000-8000-00805f9b34fb")
	private var bluetoothManager: BluetoothManager? = null
	private var bluetoothGattServer: BluetoothGattServer? = null
	private var bluetoothAdapter: BluetoothAdapter? = null
	val charStates = mutableMapOf(
		"00006970-0000-1000-8000-00805f9b34fb" to false
		, "00006971-0000-1000-8000-00805f9b34fb" to false
		, "00006972-0000-1000-8000-00805f9b34fb" to false
		, "00006973-0000-1000-8000-00805f9b34fb" to false
		, "00006974-0000-1000-8000-00805f9b34fb" to false
		, "00006975-0000-1000-8000-00805f9b34fb" to false
		, "00006976-0000-1000-8000-00805f9b34fb" to false
		, "00006977-0000-1000-8000-00805f9b34fb" to false
		, "00006978-0000-1000-8000-00805f9b34fb" to false
		, "00006979-0000-1000-8000-00805f9b34fb" to false
	)
	val healthData = mutableMapOf(
		"Steps" to "Unknown"
		, "Absolute Elevation" to "Unknown"
		, "Speed" to "Unknown"
		, "HeartRate" to "Unknown"
		, "Step per minute" to "Unknown"
		, "Pace" to "Unknown"
		, "Distance" to "Unknown"
		, "Floors" to "Unknown"
		, "Calories" to "Unknown"
		, "Elevation Gain" to "Unknown"
	)
	val dataToChar = mapOf(
		"00006970-0000-1000-8000-00805f9b34fb" to "Steps"
		,"00006971-0000-1000-8000-00805f9b34fb" to  "Absolute Elevation"
		, "00006972-0000-1000-8000-00805f9b34fb" to "Speed"
		, "00006973-0000-1000-8000-00805f9b34fb" to "HeartRate"
		, "00006974-0000-1000-8000-00805f9b34fb" to "Step per minute"
		, "00006975-0000-1000-8000-00805f9b34fb" to "Pace"
		, "00006976-0000-1000-8000-00805f9b34fb" to "Distance"
		, "00006977-0000-1000-8000-00805f9b34fb" to "Floors"
		, "00006978-0000-1000-8000-00805f9b34fb" to "Calories"
		,"00006979-0000-1000-8000-00805f9b34fb" to  "Elevation Gain"
	)
	private val charMap = mutableMapOf<String, BluetoothGattCharacteristic?>()

	private val stepscharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006970-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)
	// "Absolute Elevation" to "00006971-0000-1000-8000-00805f9b34fb"
	private val elevationcharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006971-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)
	//		, "Speed" to "00006972-0000-1000-8000-00805f9b34fb"
	private val speedcharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006972-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)

	//		, "HeartRate" to "00006973-0000-1000-8000-00805f9b34fb"
	private val heartcharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006973-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)
	//		, "Step per minute" to "00006974-0000-1000-8000-00805f9b34fb"
	private val stepspermincharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006974-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)
	//		, "Pace" to "00006975-0000-1000-8000-00805f9b34fb"
	private val pacecharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006975-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)
	//		, "Distance" to "00006976-0000-1000-8000-00805f9b34fb"
	private val distancecharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006976-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)
	//		, "Floors" to "00006977-0000-1000-8000-00805f9b34fb"
	private val floorcharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006977-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)
	//		, "Calories" to "00006978-0000-1000-8000-00805f9b34fb"
	private val caloriecharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006978-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)
	//		, "Elevation Gain" to "00006979-0000-1000-8000-00805f9b34fb"
	private val elevationGaincharacteristic = BluetoothGattCharacteristic(
		UUID.fromString("00006979-0000-1000-8000-00805f9b34fb"),
		BluetoothGattCharacteristic.PROPERTY_READ or
				BluetoothGattCharacteristic.PROPERTY_NOTIFY or
				BluetoothGattCharacteristic.PROPERTY_WRITE or
				BluetoothGattCharacteristic.PROPERTY_INDICATE, // Ensure Indicate is set
		BluetoothGattCharacteristic.PERMISSION_READ
	)

	fun startServer() {
		bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
		bluetoothAdapter = bluetoothManager?.adapter

		val gattServerCallback = object : BluetoothGattServerCallback() {
			override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
				if (newState == BluetoothProfile.STATE_CONNECTED) {
					Log.d("BLE", "Device connected: ${device.address}")
					val value = "567";
					var byteArray  =  value.toByteArray(Charsets.UTF_8)
					//sendData(device ,byteArray)
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					Log.d("BLE", "Device disconnected: ${device.address}")
				}
			}

			override fun onCharacteristicReadRequest(
				device: BluetoothDevice?, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic?
			) {
				readAndSendData(characteristic ,device, requestId)
			}
		}

		bluetoothGattServer = bluetoothManager?.openGattServer(context, gattServerCallback)
		val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)

		service.addCharacteristic(heartcharacteristic)
		charMap.set(heartcharacteristic.uuid.toString(),heartcharacteristic)
		service.addCharacteristic(elevationcharacteristic)
		charMap.set(elevationcharacteristic.uuid.toString(),elevationcharacteristic)
		service.addCharacteristic(elevationGaincharacteristic)
		charMap.set(elevationGaincharacteristic.uuid.toString(),elevationGaincharacteristic)
		service.addCharacteristic(stepscharacteristic)
		charMap.set(stepscharacteristic.uuid.toString(),stepscharacteristic)
		service.addCharacteristic(stepspermincharacteristic)
		charMap.set(stepspermincharacteristic.uuid.toString(),stepspermincharacteristic)
		service.addCharacteristic(pacecharacteristic)
		charMap.set(pacecharacteristic.uuid.toString(),pacecharacteristic)
		service.addCharacteristic(caloriecharacteristic)
		charMap.set(caloriecharacteristic.uuid.toString(),caloriecharacteristic)
		service.addCharacteristic(floorcharacteristic)
		charMap.set(floorcharacteristic.uuid.toString(),floorcharacteristic)
		service.addCharacteristic(distancecharacteristic)
		charMap.set(distancecharacteristic.uuid.toString(),distancecharacteristic)
		service.addCharacteristic(speedcharacteristic)
		charMap.set(speedcharacteristic.uuid.toString(),speedcharacteristic)

		bluetoothGattServer?.addService(service)
		Log.d("BLE", "GATT server started")
	}
	val DEVICE_NAME = "SmartWatch BLE" // Change this to your preferred name

	fun startAdvertising() {
		bluetoothAdapter?.setName(DEVICE_NAME) // Set local BLE name
		val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser
		val settings = AdvertiseSettings.Builder()
			.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
			.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
			.setConnectable(true)
			.build()

		val data = AdvertiseData.Builder()
			.setIncludeDeviceName(true)
			.addServiceUuid(ParcelUuid(SERVICE_UUID))
			.build()


		advertiser?.startAdvertising(settings, data, object : AdvertiseCallback() {
			override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
				Log.d("BLE", "Advertising started successfully")
			}

			override fun onStartFailure(errorCode: Int) {
				Log.e("BLE", "Advertising failed: $errorCode")
				//StopHealthTracking()
			}
		})
	}


	fun StartHealthTracking(bleServer: BLEServer) {
		CoroutineScope(Dispatchers.Main).launch {
			withContext(Dispatchers.Default) {
				val healthMonitor = HealthMonitor(
					HealthServices.getClient(context).exerciseClient
				)
				LogInfo("HealthMonitor initialised ")
				if (healthMonitor.IsRunning()) {
					LogInfo("A previous health tracker already started. Stopping it.")
					healthMonitor.Stop()
				}
				if (
					healthMonitor.SetUpdateCallback(healthMonitorUpdateCallback,bleServer)
					&& healthMonitor.Prepare()
					&& healthMonitor.Start()
				) {
					LogInfo("Health tracker is running")
					withContext(Dispatchers.Main) {
						globalMainActivity?.mainUiStates?.healthMonitorState?.value = true
						globalMainActivity?.mainUiStates?.message?.value = "Health tracker started"

						//	Notify("Health tracker started")
					}
				} else {
					LogError("Failed to start health tracker")
					withContext(Dispatchers.Main) {
						globalMainActivity?.mainUiStates?.healthMonitorState?.value = false
						globalMainActivity?.mainUiStates?.message?.value = "Failed to start health tracker"
					}
				}
			}

		}
	}

	fun StopHealthTracking() {
		CoroutineScope(Dispatchers.Main).launch {
			if(
				globalMainActivity?.mainUiStates?.healthMonitorState?.value == true
			) {
				withContext(Dispatchers.Default) {
					val healthMonitor = HealthMonitor(
						HealthServices.getClient(context).exerciseClient
					)
					healthMonitor.UnsetUpdateCallback(healthMonitorUpdateCallback)
					healthMonitor.Stop()
				}
				globalMainActivity?.mainUiStates?.healthMonitorState?.value = false
				globalMainActivity?.mainUiStates?.message?.value = "Health tracker stopped"
				ResetHealthData()
				//Notify("Health tracker stopped")
			}
		}
		CoroutineScope(Dispatchers.IO).launch {
			ResetCharStates()
		}
	}


	private fun readAndSendData(characteristic :BluetoothGattCharacteristic? ,device: BluetoothDevice?, requestId: Int) {
		if (characteristic != null) {
				CoroutineScope(Dispatchers.Main).launch {
					withContext(Dispatchers.IO) {
						try {
							var datatype = dataToChar[characteristic.uuid.toString()]
							var data = healthData[datatype]?.toByteArray()
							Log.i("BLE",characteristic.uuid.toString() +"--"+datatype +"--"+ healthData[datatype])

							bluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, data)
						} catch (_e: Exception) {
							Log.e("BLE", "SendData: ${_e}")
						}
					}

				}

		}
	}

	fun ResetCharStates() {
		for(key in charStates.keys) {
			charStates[key] = false
		}
	}

	fun ResetHealthData() {
		for(key in healthData.keys) {
			healthData[key] = "Unknown"
		}
	}

	 fun LogInfo(_message: String) {
		Log.i("BLE", _message)
	}

	 fun LogError(_message: String) {
		Log.e("BLE", _message)
	}
}
