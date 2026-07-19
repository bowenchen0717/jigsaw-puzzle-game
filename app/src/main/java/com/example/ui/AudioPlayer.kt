package com.example.ui

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.math.*

enum class SoundType {
    PIECE_PICKUP,
    PIECE_DROP,
    PIECE_SNAP,
    PIECE_CONNECT,
    PIECE_DRAG,
    PIECE_ROTATE,
    WRONG_PLACE,
    TICK,
    BUTTON_CLICK,
    PAGE_FLIP,
    PUZZLE_COMPLETE,
    ACHIEVEMENT
}

class AudioPlayer(private val context: Context) {
    private val executor = Executors.newSingleThreadExecutor()
    private val samplesMap = ConcurrentHashMap<SoundType, ShortArray>()
    private val tracks = ConcurrentHashMap<SoundType, AudioTrack>()
    private val sampleRate = 22050

    init {
        executor.execute {
            try {
                for (type in SoundType.values()) {
                    samplesMap[type] = generateSamples(type)
                }
            } catch (e: Exception) {
                Log.e("AudioPlayer", "Error pre-synthesizing sound samples", e)
            }
        }
    }

    private fun getOrCreateTrack(type: SoundType): AudioTrack? {
        val existing = tracks[type]
        if (existing != null) return existing

        val samples = samplesMap[type] ?: generateSamples(type).also { samplesMap[type] = it }
        return try {
            val track = createStaticTrack(samples)
            tracks[type] = track
            track
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error creating AudioTrack for $type", e)
            null
        }
    }

    private fun createStaticTrack(samples: ShortArray): AudioTrack {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val dataSizeInBytes = samples.size * 2
        val bufferSize = maxOf(minBufferSize, dataSizeInBytes)

        val builder = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            builder.setContext(context)
        }

        val track = builder.build()
        track.write(samples, 0, samples.size)
        return track
    }

    private fun generateSamples(type: SoundType): ShortArray {
        return when (type) {
            SoundType.PIECE_PICKUP -> {
                val numSamples = (0.15 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val freq = 320.0 + progress * 320.0
                    val wave = sin(2.0 * PI * freq * t)
                    val envelope = if (progress < 0.1) progress / 0.1 else (1.0 - progress) / 0.9
                    samples[i] = (wave * envelope * 22000.0).toInt().toShort()
                }
                samples
            }
            SoundType.PIECE_DROP -> {
                val numSamples = (0.20 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val freq = 130.0 - progress * 70.0
                    val wave = sin(2.0 * PI * freq * t)
                    val envelope = exp(-5.0 * progress) * (1.0 - progress)
                    val noise = if (progress < 0.2) (Math.random() * 2.0 - 1.0) * (0.2 - progress) * 0.15 else 0.0
                    samples[i] = ((wave + noise) * envelope * 20000.0).toInt().toShort()
                }
                samples
            }
            SoundType.PIECE_SNAP -> {
                val numSamples = (0.10 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val transientEnv = exp(-100.0 * t)
                    val transientWave = sin(2.0 * PI * 1000.0 * t) * transientEnv
                    val bodyEnv = exp(-20.0 * t) * (1.0 - progress)
                    val bodyWave = sin(2.0 * PI * 280.0 * t) * bodyEnv
                    samples[i] = ((transientWave * 0.7 + bodyWave * 0.3) * 28000.0).toInt().toShort()
                }
                samples
            }
            SoundType.PIECE_CONNECT -> {
                val numSamples = (0.30 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val t1 = t
                    val env1 = exp(-80.0 * t1)
                    val wave1 = sin(2.0 * PI * 850.0 * t1) * env1
                    val t2 = t - 0.06
                    val wave2 = if (t2 >= 0) {
                        val env2 = exp(-100.0 * t2)
                        sin(2.0 * PI * 1100.0 * t2) * env2
                    } else {
                        0.0
                    }
                    val combined = wave1 * 0.5 + wave2 * 0.6
                    val envelope = 1.0 - progress
                    samples[i] = (combined * envelope * 28000.0).toInt().toShort()
                }
                samples
            }
            SoundType.PIECE_DRAG -> {
                val numSamples = (0.35 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                var lastNoise = 0.0
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val rawNoise = Math.random() * 2.0 - 1.0
                    val lpNoise = lastNoise + 0.15 * (rawNoise - lastNoise)
                    lastNoise = lpNoise
                    val envelope = sin(progress * PI) * exp(-2.0 * progress)
                    samples[i] = (lpNoise * envelope * 12000.0).toInt().toShort()
                }
                samples
            }
            SoundType.PIECE_ROTATE -> {
                val numSamples = (0.18 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val phase = sin(progress * PI)
                    val freq = 450.0 + phase * 350.0 + sin(2.0 * PI * 25.0 * t) * 40.0
                    val wave = sin(2.0 * PI * freq * t)
                    val envelope = sin(progress * PI)
                    samples[i] = (wave * envelope * 22000.0).toInt().toShort()
                }
                samples
            }
            SoundType.WRONG_PLACE -> {
                val numSamples = (0.28 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val wave1 = sin(2.0 * PI * 150.0 * t)
                    val wave2 = sin(2.0 * PI * 155.0 * t)
                    val combined = (wave1 + wave2) * 0.5
                    val pulse = sin(progress * 2.0 * PI)
                    val envelope = (if (pulse > 0) pulse else 0.0) * (1.0 - progress)
                    samples[i] = (combined * envelope * 22000.0).toInt().toShort()
                }
                samples
            }
            SoundType.TICK -> {
                val numSamples = (0.04 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-120.0 * t)
                    val wave = sin(2.0 * PI * 1200.0 * t) * env
                    samples[i] = (wave * 26000.0).toInt().toShort()
                }
                samples
            }
            SoundType.BUTTON_CLICK -> {
                val numSamples = (0.06 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / sampleRate
                    val freq = 900.0 - progress * 200.0
                    val wave = sin(2.0 * PI * freq * t)
                    val env = exp(-50.0 * t) * (1.0 - progress)
                    samples[i] = (wave * env * 24000.0).toInt().toShort()
                }
                samples
            }
            SoundType.PAGE_FLIP -> {
                val numSamples = (0.22 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                var lastNoise = 0.0
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val rawNoise = Math.random() * 2.0 - 1.0
                    val filterCoeff = 0.05 + 0.15 * progress
                    val lpNoise = lastNoise + filterCoeff * (rawNoise - lastNoise)
                    lastNoise = lpNoise
                    val env = sin(progress * PI) * (1.0 - progress)
                    samples[i] = (lpNoise * env * 16000.0).toInt().toShort()
                }
                samples
            }
            SoundType.PUZZLE_COMPLETE -> {
                val numSamples = (1.80 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                val noteFreqs = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 659.25, 783.99, 1046.50)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val noteDuration = 0.10
                    val activeNotes = mutableListOf<Double>()
                    if (t < 0.7) {
                        val noteIdx = (t / noteDuration).toInt() % noteFreqs.size
                        activeNotes.add(noteFreqs[noteIdx])
                    } else {
                        activeNotes.add(noteFreqs[3])
                        activeNotes.add(noteFreqs[4])
                        activeNotes.add(noteFreqs[5])
                        activeNotes.add(noteFreqs[6])
                    }
                    var valSum = 0.0
                    for (f in activeNotes) {
                        valSum += sin(2.0 * PI * f * t)
                    }
                    val avg = if (activeNotes.isNotEmpty()) valSum / activeNotes.size else 0.0
                    val env = if (progress < 0.1) progress / 0.1 else if (progress > 0.8) (1.0 - progress) / 0.2 else 1.0
                    samples[i] = (avg * env * 22000.0).toInt().toShort()
                }
                samples
            }
            SoundType.ACHIEVEMENT -> {
                val numSamples = (0.70 * sampleRate).toInt()
                val samples = ShortArray(numSamples)
                val achFreqs = doubleArrayOf(523.25, 659.25, 783.99, 987.77, 1046.50)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val noteIdx = (t / 0.08).toInt()
                    val activeNotes = mutableListOf<Double>()
                    for (n in 0..minOf(noteIdx, achFreqs.size - 1)) {
                        activeNotes.add(achFreqs[n])
                    }
                    var valSum = 0.0
                    for (f in activeNotes) {
                        valSum += sin(2.0 * PI * f * t)
                    }
                    val avg = if (activeNotes.isNotEmpty()) valSum / activeNotes.size else 0.0
                    val env = exp(-4.0 * progress) * (1.0 - progress)
                    samples[i] = (avg * env * 24000.0).toInt().toShort()
                }
                samples
            }
        }
    }

    fun play(type: SoundType) {
        if (!AudioConfig.isEnabled) return
        executor.execute {
            try {
                val track = getOrCreateTrack(type)
                if (track != null) {
                    track.stop()
                    track.reloadStaticData()
                    track.play()
                }
            } catch (e: Exception) {
                Log.e("AudioPlayer", "Error playing sound: $type", e)
            }
        }
    }

    fun play(resId: Int) {
        playPickup()
    }

    fun playPickup() = play(SoundType.PIECE_PICKUP)
    fun playDrop() = play(SoundType.PIECE_DROP)
    fun playSnap() = play(SoundType.PIECE_SNAP)
    fun playConnect() = play(SoundType.PIECE_CONNECT)
    fun playDrag() = play(SoundType.PIECE_DRAG)
    fun playRotate() = play(SoundType.PIECE_ROTATE)
    fun playWrongPlace() = play(SoundType.WRONG_PLACE)
    fun playTick() = play(SoundType.TICK)
    fun playButtonClick() = play(SoundType.BUTTON_CLICK)
    fun playPageFlip() = play(SoundType.PAGE_FLIP)
    fun playPuzzleComplete() = play(SoundType.PUZZLE_COMPLETE)
    fun playAchievement() = play(SoundType.ACHIEVEMENT)

    fun release() {
        executor.shutdown()
        for (track in tracks.values) {
            try {
                track.release()
            } catch (e: Exception) {
                // Ignore
            }
        }
        tracks.clear()
        samplesMap.clear()
    }
}
