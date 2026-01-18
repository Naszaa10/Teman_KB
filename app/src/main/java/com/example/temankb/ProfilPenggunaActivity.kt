package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ProfilPenggunaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan layout XML ini benar-benar ada
        setContentView(R.layout.activity_profil_pengguna)

        // Tombol Lanjut (btnNextPage2) sudah ada di XML
        val btnLanjut = findViewById<Button>(R.id.btnProfil)
        btnLanjut.setOnClickListener {
//             Contoh: pindah ke halaman berikutnya atau tampil Toast
//             Misal lanjut ke halaman hasilrekomendasi
             val intent = Intent(this, kondisireproduksi::class.java)
             startActivity(intent)
        }
    }
}
