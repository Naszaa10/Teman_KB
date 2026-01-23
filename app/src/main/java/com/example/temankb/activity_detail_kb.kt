package com.example.temankb

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class activity_detail_kb : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kb)

        val jenisKB = intent.getStringExtra("jenis_kb")?.lowercase() ?: ""

        val layoutIUD = findViewById<MaterialCardView>(R.id.layoutIUD)
        val layoutImplan = findViewById<MaterialCardView>(R.id.layoutImplan)
        val layoutPil = findViewById<MaterialCardView>(R.id.layoutPil)
        val layoutSuntik = findViewById<MaterialCardView>(R.id.layoutSuntik)
        val layoutKondom = findViewById<MaterialCardView>(R.id.layoutKondom)

        val semuaLayout = listOf(
            layoutIUD, layoutImplan, layoutPil, layoutSuntik, layoutKondom
        )

        // Sembunyikan semua
        semuaLayout.forEach { it.visibility = View.GONE }

        // Tampilkan sesuai pilihan
        when (jenisKB) {
            "iud", "iud cu", "akdr" -> layoutIUD.visibility = View.VISIBLE
            "implan" -> layoutImplan.visibility = View.VISIBLE
            "pil" -> layoutPil.visibility = View.VISIBLE
            "suntik" -> layoutSuntik.visibility = View.VISIBLE
            "kondom" -> layoutKondom.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.btnKembali).setOnClickListener {
            finish()
        }
    }
}
