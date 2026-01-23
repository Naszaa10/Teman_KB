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

        // ✅ FIX EDGE TO EDGE (INI KUNCINYA)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                systemBars.bottom + 24 // ⬅️ kasih jarak dari navbar
            )
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnLanjut = findViewById<Button>(R.id.btnlanjutkb)

        btnBack.setOnClickListener {
            finish()
        }

        val emailLogin = intent.getStringExtra("EMAIL_LOGIN")

        btnLanjut.setOnClickListener {
            val intent = Intent(this, ProfilPenggunaActivity::class.java)
            intent.putExtra("EMAIL_LOGIN", emailLogin)
            startActivity(intent)
        }
    }
}
