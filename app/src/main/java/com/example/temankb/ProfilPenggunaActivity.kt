package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class ProfilPenggunaActivity : AppCompatActivity() {

    private lateinit var inputNama: TextInputEditText
    private lateinit var inputUmur: TextInputEditText
    private lateinit var btnLanjut: Button

    private var userId: String? = null
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_pengguna)

        inputNama = findViewById(R.id.inputNama)
        inputUmur = findViewById(R.id.inputUmur)
        btnLanjut = findViewById(R.id.btnProfil)

        // Ambil user pertama dari database
        val dbUsers = FirebaseDatabase.getInstance().reference.child("users")
        dbUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstUser = snapshot.children.first()
                    userId = firstUser.key
                    userName = firstUser.child("name").getValue(String::class.java)
                    inputNama.setText(userName)
                } else {
                    Toast.makeText(this@ProfilPenggunaActivity, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        btnLanjut.setOnClickListener {
            saveProfil()
        }
    }

    private fun saveProfil() {
        if (userId == null) return

        val umurText = inputUmur.text.toString()
        if (umurText.isEmpty()) {
            Toast.makeText(this, "Isi umur terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val umur = umurText.toIntOrNull()
        if (umur == null) {
            Toast.makeText(this, "Umur harus angka", Toast.LENGTH_SHORT).show()
            return
        }

        // Simpan awal ke kondisi_medis
        val kondisiAwal = KondisiMedis(
            userId = userId,
            usia = umur,
            nama = userName ?: "",
            timestamp = System.currentTimeMillis()
        )

        val db = FirebaseDatabase.getInstance().reference.child("kondisi_medis").child(userId!!)
        db.setValue(kondisiAwal).addOnSuccessListener {
            // Lanjut ke KondisiReproduksi
            val intent = Intent(this, kondisireproduksi::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Gagal simpan: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
