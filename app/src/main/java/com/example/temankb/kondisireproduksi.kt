package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class kondisireproduksi : AppCompatActivity() {

    private lateinit var radio1: RadioGroup
    private lateinit var radio2: RadioGroup
    private lateinit var radio3: RadioGroup
    private lateinit var radio4: RadioGroup
    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var checkBox4: CheckBox
    private lateinit var checkBox5: CheckBox
    private lateinit var checkBox6: CheckBox
    private lateinit var btnNext: Button

    private var userId: String? = null
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kondisireproduksi)

        userId = intent.getStringExtra("userId")
        if (userId == null) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbRef = FirebaseDatabase.getInstance().reference.child("kondisi_medis").child(userId!!)

        // Radio groups
        radio1 = findViewById(R.id.radioGroup1)
        radio2 = findViewById(R.id.radioGroup2)
        radio3 = findViewById(R.id.radioGroup3)
        radio4 = findViewById(R.id.radioGroup4)

        // Checkboxes
        checkBox1 = findViewById(R.id.chb1)
        checkBox2 = findViewById(R.id.chb2)
        checkBox3 = findViewById(R.id.chb3)
        checkBox4 = findViewById(R.id.chb4)
        checkBox5 = findViewById(R.id.chb5)
        checkBox6 = findViewById(R.id.chb6)

        btnNext = findViewById(R.id.btnReproduksi)

        btnNext.setOnClickListener {
            saveKondisiReproduksi()
        }
    }

    private fun saveKondisiReproduksi() {
        if (radio1.checkedRadioButtonId == -1 || radio2.checkedRadioButtonId == -1 ||
            radio3.checkedRadioButtonId == -1 || radio4.checkedRadioButtonId == -1
        ) {
            Toast.makeText(this, "Harap isi semua pilihan", Toast.LENGTH_SHORT).show()
            return
        }

        val kondisi4 = findViewById<RadioButton>(radio1.checkedRadioButtonId).text.toString()
        val kondisi5 = findViewById<RadioButton>(radio2.checkedRadioButtonId).text.toString()
        val kondisi6 = findViewById<RadioButton>(radio3.checkedRadioButtonId).text.toString()
        val kondisi7 = findViewById<RadioButton>(radio4.checkedRadioButtonId).text.toString()

        val kondisi8 = mutableListOf<String>()
        if (checkBox1.isChecked) kondisi8.add(checkBox1.text.toString())
        if (checkBox2.isChecked) kondisi8.add(checkBox2.text.toString())
        if (checkBox3.isChecked) kondisi8.add(checkBox3.text.toString())
        if (checkBox4.isChecked) kondisi8.add(checkBox4.text.toString())
        if (checkBox5.isChecked) kondisi8.add(checkBox5.text.toString())
        if (checkBox6.isChecked) kondisi8.add(checkBox6.text.toString())

        // Ambil data lama dan update
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existing = snapshot.getValue(KondisiMedis::class.java)
                val updated = existing?.copy(
                    kondisi4 = kondisi4,
                    kondisi5 = kondisi5,
                    kondisi6 = kondisi6,
                    kondisi7 = kondisi7,
                    kondisi8 = kondisi8,
                    timestamp = System.currentTimeMillis()
                ) ?: KondisiMedis(
                    userId = userId,
                    kondisi4 = kondisi4,
                    kondisi5 = kondisi5,
                    kondisi6 = kondisi6,
                    kondisi7 = kondisi7,
                    kondisi8 = kondisi8,
                    timestamp = System.currentTimeMillis()
                )

                dbRef.setValue(updated).addOnSuccessListener {
                    Toast.makeText(this@kondisireproduksi, "Data tersimpan", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@kondisireproduksi, preferensi_pengguna::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(this@kondisireproduksi, "Gagal simpan: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
