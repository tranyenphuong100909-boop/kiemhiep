package com.example.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object SoundSystem {
    private var isMuted = false
    private var musicJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) {
            musicJob?.cancel()
        }
    }

    fun isAudioMuted(): Boolean = isMuted

    // Play a generated short tone or sound effect
    private fun playTone(
        frequencies: List<Float>,
        durationMs: Int,
        decay: Boolean = true,
        noiseAmount: Float = 0f
    ) {
        if (isMuted) return
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
                if (numSamples <= 0) return@launch
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val time = i.toFloat() / sampleRate
                    var sample = 0f

                    for (f in frequencies) {
                        sample += sin(2.0 * PI * f * time).toFloat()
                    }
                    sample /= (frequencies.size.coerceAtLeast(1))

                    if (noiseAmount > 0f) {
                        val noise = (Math.random().toFloat() * 2f - 1f) * noiseAmount
                        sample = sample * (1f - noiseAmount) + noise
                    }

                    val envelope = if (decay) {
                        val progress = i.toFloat() / numSamples
                        (1f - progress)
                    } else 1f

                    val finalSample = (sample * envelope * 0.4f).coerceIn(-1f, 1f)
                    buffer[i] = (finalSample * Short.MAX_VALUE).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                delay(durationMs.toLong() + 50)
                audioTrack.release()
            } catch (_: Exception) {
                // Ignore audio errors gracefully on restricted devices
            }
        }
    }

    // Specific combat SFX
    fun playSwordSlash() {
        playTone(listOf(440f, 660f, 880f), 120, decay = true, noiseAmount = 0.35f)
    }

    fun playFireBurst() {
        playTone(listOf(130f, 180f, 260f), 280, decay = true, noiseAmount = 0.6f)
    }

    fun playIceShatter() {
        playTone(listOf(1050f, 1500f, 2100f), 200, decay = true, noiseAmount = 0.15f)
    }

    fun playHealChime() {
        playTone(listOf(523.25f, 659.25f, 783.99f, 1046.50f), 450, decay = true, noiseAmount = 0.0f)
    }

    fun playDragonRoar() {
        playTone(listOf(65f, 98f, 110f), 600, decay = true, noiseAmount = 0.7f)
    }

    fun playLevelUp() {
        scope.launch {
            val notes = listOf(440f, 554.37f, 659.25f, 880f)
            for (note in notes) {
                playTone(listOf(note), 150, decay = true)
                delay(90)
            }
        }
    }

    fun playButtonClick() {
        playTone(listOf(800f), 40, decay = true)
    }

    fun playChestOpen() {
        scope.launch {
            playTone(listOf(523f), 100)
            delay(100)
            playTone(listOf(659f), 100)
            delay(100)
            playTone(listOf(784f, 1046f), 300)
        }
    }

    // Oriental Ambient Pentatonic BGM Loop
    fun startBgm() {
        if (isMuted || musicJob?.isActive == true) return
        musicJob = scope.launch {
            // Pentatonic scale notes (Gong, Shang, Jiao, Zhi, Yu in D Major: D, E, F#, A, B)
            val melody = listOf(
                293.66f, 329.63f, 369.99f, 440.00f, 493.88f,
                587.33f, 493.88f, 440.00f, 369.99f, 293.66f
            )
            var index = 0
            while (isActive && !isMuted) {
                val freq = melody[index % melody.size]
                playTone(listOf(freq, freq * 1.5f), 400, decay = true, noiseAmount = 0.05f)
                index++
                delay(800)
            }
        }
    }

    fun stopBgm() {
        musicJob?.cancel()
        musicJob = null
    }
}
