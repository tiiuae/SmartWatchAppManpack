/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.tii.dcsmartwatchcode.presentation

import android.Manifest
import android.app.KeyguardManager
import android.app.KeyguardManager.KeyguardLock
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.health.services.client.HealthServices
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.tii.dcsmartwatchcode.R
import com.tii.dcsmartwatchcode.presentation.theme.DCSmartwatchCodeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainUiStates {
	var message = mutableStateOf("Scan for devices")
	var prevDeviceButtonState = mutableStateOf(false)
	var selectedDeviceName = mutableStateOf("")
	var nextDeviceButtonState = mutableStateOf(false)
	var bleScanButtonState = mutableStateOf(true)
	var bleScanState = mutableStateOf(false)
	var bleConnectButtonState = mutableStateOf(false)
	var bleConnectionState = mutableStateOf(false)
	var healthMonitorButtonState = mutableStateOf(false)
	var healthMonitorState = mutableStateOf(false)
}
var globalMainActivity: MainActivity? = null

class MainActivity : ComponentActivity() {
	private lateinit var bleServer: BLEServer
	val mainUiStates = MainUiStates()
	private lateinit var keyguardLock : KeyguardLock
	private val bluetoothPermissions = listOf(
		android.Manifest.permission.BLUETOOTH,
		android.Manifest.permission.BLUETOOTH_ADMIN,
		android.Manifest.permission.BLUETOOTH_CONNECT,
		android.Manifest.permission.BLUETOOTH_SCAN,
		android.Manifest.permission.BLUETOOTH_ADVERTISE,
		android.Manifest.permission.ACCESS_FINE_LOCATION,
		android.Manifest.permission.ACCESS_COARSE_LOCATION
	)
	private val foregroundServicePermissions = listOf(
		android.Manifest.permission.FOREGROUND_SERVICE,
		android.Manifest.permission.FOREGROUND_SERVICE_LOCATION,
		android.Manifest.permission.POST_NOTIFICATIONS
	)
	private val healthServicesPermissions = listOf(
		android.Manifest.permission.ACTIVITY_RECOGNITION,
		android.Manifest.permission.BODY_SENSORS,
		android.Manifest.permission.BODY_SENSORS_BACKGROUND,
		android.Manifest.permission.ACCESS_FINE_LOCATION
	)
	val permissionLauncher = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { _ ->
		{}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		installSplashScreen()

		super.onCreate(savedInstanceState)

		setTheme(android.R.style.Theme_DeviceDefault)

		setContent {
			WearApp("Android")
		}
//		if(CheckAndRequestPermission(foregroundServicePermissions)) {
//			return
//		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			ActivityCompat.requestPermissions(
				this, // ✅ Ensure 'this' is an Activity (MainActivity or AppCompatActivity)
				arrayOf(
					Manifest.permission.BLUETOOTH_ADVERTISE,
					Manifest.permission.BLUETOOTH_CONNECT,
					Manifest.permission.BLUETOOTH_SCAN
				),
				1
			)
		}

		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

		bleServer = BLEServer(this)

		bleServer.startServer()
		bleServer.startAdvertising()
		bleServer.StartHealthTracking(bleServer)

	}

	override fun onResume() {
		super.onResume()
		if(bleServer != null)
		bleServer.StartHealthTracking(bleServer)
	}

	override fun onPause() {
		super.onPause()
		bleServer.StopHealthTracking()
	}
	override fun onStop() {
		super.onStop()
		bleServer.StopHealthTracking()

	}

	private fun CheckAndRequestPermission(_permission: List<String>): Boolean {
		var requestPermission = ""
		for (i in _permission) {
			if (
				ContextCompat.checkSelfPermission(this, i)
				!= PackageManager.PERMISSION_GRANTED
			) {
				Log.i("BLE", "${i.toString()} is not yet granted")
				requestPermission = i
				break
			}
		}
		if (requestPermission != "") {
			Log.i("BLE", "Launching request permission")
			permissionLauncher.launch(requestPermission)
			return true
		}
		return false
	}
}
@Composable
fun WearApp(greetingName: String) {
	DCSmartwatchCodeTheme {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colors.background),
			contentAlignment = Alignment.Center
		) {
			TimeText()
			Greeting(greetingName = greetingName)
		}
	}
}

@Composable
fun Greeting(greetingName: String) {
	Text(
		modifier = Modifier.fillMaxWidth(),
		textAlign = TextAlign.Center,
		color = MaterialTheme.colors.primary,
		text = stringResource(R.string.hello_world, greetingName)
	)
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
	WearApp("Preview Android")
}