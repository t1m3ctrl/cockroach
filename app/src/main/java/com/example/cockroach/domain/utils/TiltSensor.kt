package com.example.cockroach.domain.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Класс для работы с акселерометром и определения наклона устройства
 */
class TiltSensor(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _tiltAcceleration = MutableStateFlow(Offset.Zero)
    val tiltAcceleration: StateFlow<Offset> = _tiltAcceleration

    private var isListening = false

    /**
     * Начинает отслеживание наклона устройства
     */
    fun startListening() {
        if (!isListening && accelerometer != null) {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            isListening = true
        }
    }

    /**
     * Останавливает отслеживание наклона устройства
     */
    fun stopListening() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
            _tiltAcceleration.value = Offset.Zero
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                // event.values[0] - наклон по оси X (влево/вправо)
                // event.values[1] - наклон по оси Y (вперед/назад)
                // event.values[2] - наклон по оси Z (вверх/вниз) нам не нужно

                // Нормализуем значения (обычно от -10 до 10)
                // Инвертируем X и Y для естественного ощущения наклона
                val x = -it.values[0] / 10f
                val y = it.values[1] / 10f

                _tiltAcceleration.value = Offset(x, y)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun release() {
        stopListening()
    }
}
