package com.github.lyra.android

import android.content.Context
import java.io.File

object Lyra {
    init {
        System.loadLibrary("lyra_jni")
    }

    private lateinit var weightsDirectory: String

    fun setup(context: Context) {
        weightsDirectory = context.getExternalFilesDir(null)?.absolutePath
            ?: error("externalFilesDir is not available")
        val assets = context.assets
        arrayOf(
            "lyra_config.binarypb",
            "lyragan.tflite",
            "quantizer.tflite",
            "soundstream_encoder.tflite",
        ).forEach { fileName ->
            val outputFile = File(weightsDirectory, fileName)
            assets.open(fileName).copyTo(outputFile.outputStream())
        }
    }

    fun createEncoder(
        sampleRate: Int,
        channels: Int,
        bitrate: Int,
        enableDtx: Boolean = false,
    ): LyraEncoder {
        return LyraEncoder(sampleRate, channels, bitrate, enableDtx, weightsDirectory)
    }
}