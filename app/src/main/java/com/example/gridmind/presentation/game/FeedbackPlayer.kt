package com.example.gridmind.presentation.game

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.gridmind.domain.model.GameSettings

/** Plays result sound / vibration according to user settings. */
class FeedbackPlayer(context: Context) {
    private val appContext = context.applicationContext
    private var tone: ToneGenerator? = null

    fun play(success: Boolean, settings: GameSettings) {
        if (settings.soundEnabled) {
            val generator = tone ?: runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, 60) }
                .getOrNull()?.also { tone = it }
            generator?.startTone(
                if (success) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_NACK,
                200
            )
        }
        if (settings.vibrationEnabled) vibrate(if (success) 80L else 250L)
    }

    private fun vibrate(millis: Long) {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            appContext.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        if (vibrator?.hasVibrator() == true) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun release() {
        tone?.release()
        tone = null
    }
}
