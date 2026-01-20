package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class preferensi_pengguna : AppCompatActivity() {

    private lateinit var radio1: RadioGroup
    private lateinit var radio2: RadioGroup
    private lateinit var radio3: RadioGroup
    private lateinit var radio4: RadioGroup
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageView

    private lateinit var dbRef: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferensi_pengguna)

        // ==== USER ID ====
        userId = intent.getStringExtra("userId")
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ==== DATABASE ====
        dbRef = FirebaseDatabase.getInstance()
            .reference
            .child("kondisi_medis")
            .child(userId!!)

        // ==== VIEW ====
        radio1 = findViewById(R.id.pref1)
        radio2 = findViewById(R.id.pref2)
        radio3 = findViewById(R.id.pref3)
        radio4 = findViewById(R.id.pref4)
        btnNext = findViewById(R.id.btnPreferensi)
        btnBack = findViewById(R.id.btnBack)

        // ==== BACK (IMAGE) ====
        btnBack.setOnClickListener {
            kembaliKeKondisi()
        }

        // ==== NEXT ====
        btnNext.setOnClickListener {
            simpanPreferensi()
        }
    }

    // 🔙 BACK HP
    override fun onBackPressed() {
        kembaliKeKondisi()
    }

    // 🔁 SATU PINTU BACK
    private fun kembaliKeKondisi() {
        val intent = Intent(this, kondisireproduksi::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }

    private fun simpanPreferensi() {
        if (radio1.checkedRadioButtonId == -1 ||
            radio2.checkedRadioButtonId == -1 ||
            radio3.checkedRadioButtonId == -1 ||
            radio4.checkedRadioButtonId == -1
        ) {
            Toast.makeText(this, "Harap isi semua pilihan", Toast.LENGTH_SHORT).show()
            return
        }

        val data = mapOf(
            "Alat kontrasepsi jangka panjang" to findViewById<RadioButton>(radio1.checkedRadioButtonId).text.toString(),
            "Lupa minum pil setiap hari" to findViewById<RadioButton>(radio2.checkedRadioButtonId).text.toString(),
            "Butuhkan perlindungan infeksi (IMS/HIV)" to findViewById<RadioButton>(radio3.checkedRadioButtonId).text.toString(),
            "(implan/AKDR)" to findViewById<RadioButton>(radio4.checkedRadioButtonId).text.toString(),
            "timestamp" to System.currentTimeMillis()
        )

        dbRef.updateChildren(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Preferensi berhasil disimpan", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HasilRekomendasiActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan preferensi", Toast.LENGTH_LONG).show()
            }
    }
}
