package com.niusounds.lyra.example

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.lyra.android.Lyra
import com.niusounds.lyra.example.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startRecording()
            }
        }

    private var audioThread: Thread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)

            button.setOnClickListener { permission.launch(android.Manifest.permission.RECORD_AUDIO) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioThread?.let { it.interrupt();it.join() }
        audioThread = null
    }

    private fun startRecording() {
        binding.button.isEnabled = false

//        Lyra.setup(applicationContext)
        audioThread = thread {

            val record = createAudioRecord(
                sampleRate = 44100,
                channels = 1,
            )

            try {
                record.startRecording()

                val audioData = ShortArray(record.bufferSizeInFrames)
//                val encoder = Lyra.createEncoder(
//                    sampleRate = 16000, channels = 1, bitrate = 3200,
//                )
                while (!Thread.interrupted()) {
                    val readSize = record.read(audioData, 0, audioData.size)
                    if (readSize < 0) {
                        error("cannot read audio $readSize")
                    }

                    println(readSize)
                }
                println(record)
            } finally {
                record.release()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun createAudioRecord(sampleRate: Int, channels: Int): AudioRecord {
        val channelMask = when (channels) {
            1 -> AudioFormat.CHANNEL_IN_MONO
            2 -> AudioFormat.CHANNEL_IN_STEREO
            else -> error("Not supported channels")
        }
        return AudioRecord.Builder()
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(channelMask)
                    .build()
            )
            .setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            .build()
    }
}