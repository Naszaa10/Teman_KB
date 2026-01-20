package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView

class tentangkb : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tentangkb)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnLanjut = findViewById<Button>(R.id.btnlanjutkb)

        // 🔙 BACK → kembali ke MainActivity
        btnBack.setOnClickListener {
            finish() // ⬅️ INI KUNCINYA
        }

        val emailLogin = intent.getStringExtra("EMAIL_LOGIN")

        btnLanjut.setOnClickListener {
            val intent = Intent(this, ProfilPenggunaActivity::class.java)
            intent.putExtra("EMAIL_LOGIN", emailLogin)
            startActivity(intent)
        }
    }
}