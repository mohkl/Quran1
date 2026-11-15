package com.rfm.quran.data

import android.media.AudioAttributes
import android.media.MediaPlayer

object AudioPlayer {
    private var mp: MediaPlayer? = null
    var currentUrl: String? = null
        private set

    fun play(
        url: String,
        onPrepared: () -> Unit = {},
        onError: () -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        stop()
        val m = MediaPlayer()
        mp = m
        currentUrl = url
        m.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        try {
            m.setDataSource(url)
            m.setOnPreparedListener { onPrepared() }
            m.setOnCompletionListener { currentUrl = null; onComplete() }
            m.setOnErrorListener { _, _, _ -> currentUrl = null; onError(); true }
            m.prepareAsync()
        } catch (e: Exception) {
            currentUrl = null
            onError()
        }
    }

    fun pause() { mp?.pause() }
    fun resume() { mp?.start() }
    fun isPlaying(): Boolean = mp?.isPlaying == true

    fun stop() {
        try { mp?.stop() } catch (_: Exception) {}
        mp?.release()
        mp = null
        currentUrl = null
    }
}
