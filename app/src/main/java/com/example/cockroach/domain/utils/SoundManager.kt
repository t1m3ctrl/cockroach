package com.example.cockroach.domain.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes

/**
 * Менеджер для управления звуковыми эффектами в игре
 */
class SoundManager(private val context: Context) {
    private var beetleScreamPlayer: MediaPlayer? = null

    /**
     * Воспроизводит звук "крика жуков" при активации режима наклона
     */
    fun playBeetleScream(@RawRes soundResId: Int) {
        try {
            // Останавливаем предыдущее воспроизведение, если оно было
            beetleScreamPlayer?.release()

            // Создаем новый медиаплеер
            beetleScreamPlayer = MediaPlayer.create(context, soundResId)
            beetleScreamPlayer?.apply {
                setOnCompletionListener { mp ->
                    mp.release()
                }
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Если звуковой файл не найден, просто продолжаем без звука
        }
    }

    /**
     * Останавливает все активные звуки
     */
    fun stopAllSounds() {
        beetleScreamPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        beetleScreamPlayer = null
    }

    /**
     * Освобождает ресурсы менеджера звуков
     */
    fun release() {
        stopAllSounds()
    }
}
