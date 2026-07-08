package com.example.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.R

class AudioPlayer(context: Context) {
    private val soundPool = SoundPool.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .setMaxStreams(5)
        .build()

    // Map to store loaded sound IDs
    private val soundIds = mutableMapOf<Int, Int>()

    init {
        // To use sounds, create res/raw folder and add files like click.mp3, success.mp3
        // Then uncomment and load them:
        // soundIds[R.raw.click] = soundPool.load(context, R.raw.click, 1)
        // soundIds[R.raw.success] = soundPool.load(context, R.raw.success, 1)
    }

    fun play(resId: Int) {
        if (AudioConfig.isEnabled) {
            soundIds[resId]?.let { soundPool.play(it, 1f, 1f, 0, 0, 1f) }
        }
    }

    fun release() {
        soundPool.release()
    }
}
