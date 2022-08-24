package net.davegoddin.swaysensor

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class AccelerometerManager(activity: Activity, val changeListener: (values: FloatArray) -> Unit) : SensorEventListener {
    private val sensorManager: SensorManager =  activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelHelp : AccelerometerHelper = AccelerometerHelper()

    var accelX : Float = 0f
    var accelY : Float = 0f
    var accelZ : Float = 0f

    fun registerListener() {
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        deviceSensors.forEach{ sensor -> Log.d("sensors", sensor.stringType)}

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun unregisterListener()
    {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        changeListener(event.values)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }


}