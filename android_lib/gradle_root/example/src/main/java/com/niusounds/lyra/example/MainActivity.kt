package com.niusounds.lyra.example

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.lyra.android.Lyra
import com.niusounds.lyra.example.databinding.ActivityMainBinding
import java.nio.ByteBuffer
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

        Lyra.setup(applicationContext)
        audioThread = thread {

            val record = createAudioRecord(
                sampleRate = 16000,
                channels = 1,
            )
            val track = createAudioTrack(
                sampleRate = 16000,
                channels = 1,
            )
            val encoder = Lyra.createEncoder(
                sampleRate = 16000,
                channels = 1,
                bitrate = 9200,
            )
            val decoder = Lyra.createDecoder(
                sampleRate = 16000,
                channels = 1,
            )
            try {
                record.startRecording()
                track.play()

                val audioData = ShortArray(320)

                val encoded = ByteBuffer.allocateDirect(1024)
                val decoded = ShortArray(320)

                while (!Thread.interrupted()) {
                    val readSize = record.read(audioData, 0, audioData.size)
                    if (readSize < 0) {
                        error("cannot read audio $readSize")
                    }

                    val encodedSize = encoder.encode(audioData, readSize, encoded)

//                    println("readSize: $readSize encodedSize: $encodedSize")

                    val setEncodedPacketResult = decoder.setEncodedPacket(encoded, encodedSize)
                    if (setEncodedPacketResult) {
                        val decodeResult = decoder.decodeSamples(readSize, decoded)
                        println("readSize: $readSize encodedSize: $encodedSize decodeResult: $decodeResult")
                        track.write(decoded, 0, decoded.size)
                    } else {
                        track.write(audioData, 0, audioData.size)
                    }
                }
            } finally {
                record.release()
                track.release()
                encoder.release()
                decoder.release()
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

    private fun createAudioTrack(sampleRate: Int, channels: Int): AudioTrack {
        val channelMask = when (channels) {
            1 -> AudioFormat.CHANNEL_OUT_MONO
            2 -> AudioFormat.CHANNEL_OUT_STEREO
            else -> error("Not supported channels")
        }
        return AudioTrack.Builder()
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(channelMask)
                    .build()
            )
            .build()
    }
}