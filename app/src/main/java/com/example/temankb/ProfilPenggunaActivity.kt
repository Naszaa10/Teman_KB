package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class ProfilPenggunaActivity : AppCompatActivity() {

    private lateinit var inputNama: TextInputEditText
    private lateinit var inputUmur: TextInputEditText
    private lateinit var btnLanjut: Button
    private lateinit var btnBack: ImageView

    private lateinit var rgHamil: RadioGroup
    private lateinit var rgKanker: RadioGroup
    private lateinit var rgPendarahan: RadioGroup

    private var userId: String? = null
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_pengguna)

        // ==== INIT VIEW ====
        inputNama = findViewById(R.id.inputNama)
        inputUmur = findViewById(R.id.inputUmur)
        btnLanjut = findViewById(R.id.btnProfil)
        btnBack = findViewById(R.id.btnBack)

        rgHamil = findViewById(R.id.radioGroup3)
        rgKanker = findViewById(R.id.radioGroup4)
        rgPendarahan = findViewById(R.id.radioGroup5)

        // ==== BACK ====
        btnBack.setOnClickListener {
            finish()
        }

        // ==== AMBIL EMAIL DARI LOGIN ====
        userEmail = intent.getStringExtra("EMAIL_LOGIN")
        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        ambilUserDariFirebase(userEmail!!)

        btnLanjut.setOnClickListener {
            validasiDanSimpan()
        }
    }

    // ================= AMBIL USER =================
    private fun ambilUserDariFirebase(email: String) {
        FirebaseDatabase.getInstance().reference
            .child("users")
            .orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(
                            this@ProfilPenggunaActivity,
                            "User tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    for (userSnap in snapshot.children) {
                        userId = userSnap.key
                        val nama = userSnap.child("name").value?.toString()
                        inputNama.setText(nama)
                        break
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ================= VALIDASI =================
    private fun validasiDanSimpan() {
        val nama = inputNama.text.toString().trim()
        val umurText = inputUmur.text.toString().trim()

        if (nama.isEmpty()) {
            inputNama.error = "Nama wajib diisi"
            return
        }

        if (umurText.isEmpty()) {
            inputUmur.error = "Umur wajib diisi"
            return
        }

        val umur = umurText.toIntOrNull()
        if (umur == null) {
            inputUmur.error = "Umur harus angka"
            return
        }

        if (rgHamil.checkedRadioButtonId == -1 ||
            rgKanker.checkedRadioButtonId == -1 ||
            rgPendarahan.checkedRadioButtonId == -1
        ) {
            Toast.makeText(this, "Semua pertanyaan wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val hamil =
            findViewById<RadioButton>(rgHamil.checkedRadioButtonId).text.toString()
        val kanker =
            findViewById<RadioButton>(rgKanker.checkedRadioButtonId).text.toString()
        val pendarahan =
            findViewById<RadioButton>(rgPendarahan.checkedRadioButtonId).text.toString()

        simpanKeFirebase(nama, umur, hamil, kanker, pendarahan)
    }

    // ================= SIMPAN KE FIREBASE (FIX) =================
    private fun simpanKeFirebase(
        nama: String,
        umur: Int,
        hamil: String,
        kanker: String,
        pendarahan: String
    ) {
        val data = mapOf(
            "userId" to userId,
            "nama" to nama,
            "usia" to umur,

            // 🔥 HARUS SAMA DENGAN MODEL KondisiMedis
            "kondisi1" to hamil,
            "kondisi2" to kanker,
            "kondisi3" to pendarahan,

            "timestamp" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance().reference
            .child("kondisi_medis")
            .child(userId!!)
            .setValue(data)
            .addOnSuccessListener {

                val intent = Intent(this, kondisireproduksi::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
    }
}
