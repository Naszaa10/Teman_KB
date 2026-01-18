package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class preferensi_pengguna : AppCompatActivity() {

    private lateinit var radio1: RadioGroup
    private lateinit var radio2: RadioGroup
    private lateinit var radio3: RadioGroup
    private lateinit var radio4: RadioGroup
    private lateinit var btnNext: Button

    private var userId: String? = null
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferensi_pengguna)

        userId = intent.getStringExtra("userId")
        if (userId == null) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbRef = FirebaseDatabase.getInstance().reference.child("kondisi_medis").child(userId!!)

        radio1 = findViewById(R.id.pref1)
        radio2 = findViewById(R.id.pref2)
        radio3 = findViewById(R.id.pref3)
        radio4 = findViewById(R.id.pref4)
        btnNext = findViewById(R.id.btnPreferensi)

        btnNext.setOnClickListener {
            savePreferensi()
        }
    }

    private fun savePreferensi() {
        if (radio1.checkedRadioButtonId == -1 || radio2.checkedRadioButtonId == -1 ||
            radio3.checkedRadioButtonId == -1 || radio4.checkedRadioButtonId == -1
        ) {
            Toast.makeText(this, "Harap isi semua pilihan", Toast.LENGTH_SHORT).show()
            return
        }

        val preferensi1 = findViewById<RadioButton>(radio1.checkedRadioButtonId).text.toString()
        val preferensi2 = findViewById<RadioButton>(radio2.checkedRadioButtonId).text.toString()
        val preferensi3 = findViewById<RadioButton>(radio3.checkedRadioButtonId).text.toString()
        val preferensi4 = findViewById<RadioButton>(radio4.checkedRadioButtonId).text.toString()

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existing = snapshot.getValue(KondisiMedis::class.java)
                val updated = existing?.copy(
                    preferensi1 = preferensi1,
                    preferensi2 = preferensi2,
                    preferensi3 = preferensi3,
                    preferensi4 = preferensi4,
                    timestamp = System.currentTimeMillis()
                ) ?: KondisiMedis(
                    userId = userId,
                    preferensi1 = preferensi1,
                    preferensi2 = preferensi2,
                    preferensi3 = preferensi3,
                    preferensi4 = preferensi4,
                    timestamp = System.currentTimeMillis()
                )

                dbRef.setValue(updated).addOnSuccessListener {
                    Toast.makeText(this@preferensi_pengguna, "Data Preferensi tersimpan", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@preferensi_pengguna, HasilRekomendasiActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    finish()
                // Lanjut ke hasil rekomendasi atau halaman akhir
                }.addOnFailureListener { e ->
                    Toast.makeText(this@preferensi_pengguna, "Gagal simpan: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
