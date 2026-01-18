package com.example.temankb

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HasilRekomendasiActivity : AppCompatActivity() {

    private lateinit var tvRekomendasi: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasilrekomendasi)

        tvRekomendasi = findViewById(R.id.kb) // TextView di card pertama

        val rekomendasi = intent.getStringExtra("rekomendasi")
        tvRekomendasi.text = rekomendasi
    }
}
