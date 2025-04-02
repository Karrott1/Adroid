package com.example.th05_09

import android.annotation.SuppressLint
import android.content.Context.SENSOR_SERVICE
import androidx.core.content.ContextCompat.getSystemService

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import kotlin.math.roundToInt

@SuppressLint("RestrictedApi")
class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private lateinit var imgCompass: ImageView
    private lateinit var tvHeading: TextView

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)

    private var azimuth = 0f // Góc lệch so với hướng Bắc
    private var currentAzimuth = 0f // Giá trị hiện tại để làm mượt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgCompass = findViewById(R.id.imgCompass)
        tvHeading = findViewById(R.id.tvHeading)

        // Lấy cảm biến từ hệ thống
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(it.values, 0, gravity, 0, it.values.size)
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    System.arraycopy(it.values, 0, geomagnetic, 0, it.values.size)
                }
            }

            val rotationMatrix = FloatArray(9)
            val orientation = FloatArray(3)

            if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                SensorManager.getOrientation(rotationMatrix, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360 // Chuyển giá trị về 0° - 360°

                // Làm mượt chuyển động
                val deltaAzimuth = azimuth - currentAzimuth
                currentAzimuth += deltaAzimuth * 0.1f

                // Xoay kim la bàn
                imgCompass.rotation = -currentAzimuth

                // Cập nhật góc trên TextView
                tvHeading.text = "Hướng Bắc: ${currentAzimuth.roundToInt()}°"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Không cần xử lý trong ứng dụng này
    }
}
