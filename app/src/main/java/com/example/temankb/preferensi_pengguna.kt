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

        btnBack.setOnClickListener {
            kembaliKeKondisi()
        }

        btnNext.setOnClickListener {
            simpanPreferensi()
        }
    }

    override fun onBackPressed() {
        kembaliKeKondisi()
    }

    private fun kembaliKeKondisi() {
        val intent = Intent(this, kondisireproduksi::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }

    // ================= SIMPAN PREFERENSI =================
    private fun simpanPreferensi() {
        if (
            radio1.checkedRadioButtonId == -1 ||
            radio2.checkedRadioButtonId == -1 ||
            radio3.checkedRadioButtonId == -1 ||
            radio4.checkedRadioButtonId == -1
        ) {
            Toast.makeText(this, "Harap isi semua pilihan", Toast.LENGTH_SHORT).show()
            return
        }

        val preferensi1 = getRadioText(radio1)
        val preferensi2 = getRadioText(radio2)
        val preferensi3 = getRadioText(radio3)
        val preferensi4 = getRadioText(radio4)

        val data = mapOf(
            "preferensi1" to preferensi1,
            "preferensi2" to preferensi2,
            "preferensi3" to preferensi3,
            "preferensi4" to preferensi4,
            "timestamp" to System.currentTimeMillis()
        )

        dbRef.updateChildren(data)
            .addOnSuccessListener {
                val intent = Intent(this, HasilRekomendasiActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan preferensi", Toast.LENGTH_LONG).show()
            }
    }

    private fun getRadioText(radioGroup: RadioGroup): String {
        return findViewById<RadioButton>(
            radioGroup.checkedRadioButtonId
        ).text.toString()
    }
}
