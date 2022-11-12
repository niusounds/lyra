package com.niusounds.lyra.example

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.lyra.android.LyraEncoder
import com.niusounds.lyra.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startRecording()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)

            button.setOnClickListener { permission.launch(android.Manifest.permission.RECORD_AUDIO) }
        }
    }

    private fun startRecording() {
//TODO
    }
}