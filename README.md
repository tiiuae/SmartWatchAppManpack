# SmartWatchAppManpack
 SmartWatchAppManpack can be used to publish health related data from a smartwatch to the manpack
 using Arduino BLE connectivity and later can be pushed to NATS.
This app  make smartwatch as a peripheral device which publishes data on below characteristic UUID under 
 SericeUUID = "00006969-0000-1000-8000-00805f9b34fb" 

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

Nats data can be pushed under telemetry.sensor_reading.<heartbeat,calorie,steps,floor,speed,pace,distance,elevation>
