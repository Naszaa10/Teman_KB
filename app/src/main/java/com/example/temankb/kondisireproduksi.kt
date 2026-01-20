package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class kondisireproduksi : AppCompatActivity() {

    // ===== RADIO GROUP =====
    private lateinit var rgMenyusui: RadioGroup
    private lateinit var rgUsiaBayi: RadioGroup
    private lateinit var rgUsia35: RadioGroup
    private lateinit var rgPerokok: RadioGroup

    // ===== CHECKBOX =====
    private lateinit var chb1: CheckBox
    private lateinit var chb2: CheckBox
    private lateinit var chb3: CheckBox
    private lateinit var chb4: CheckBox
    private lateinit var chb5: CheckBox
    private lateinit var chb6: CheckBox

    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageView

    private var userId: String? = null
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kondisireproduksi)

        // ===== INIT VIEW =====
        rgMenyusui = findViewById(R.id.radioGroup1)
        rgUsiaBayi = findViewById(R.id.radioGroup2)
        rgUsia35 = findViewById(R.id.radioGroup3)
        rgPerokok = findViewById(R.id.radioGroup4)

        chb1 = findViewById(R.id.chb1)
        chb2 = findViewById(R.id.chb2)
        chb3 = findViewById(R.id.chb3)
        chb4 = findViewById(R.id.chb4)
        chb5 = findViewById(R.id.chb5)
        chb6 = findViewById(R.id.chb6)

        btnNext = findViewById(R.id.btnReproduksi)
        btnBack = findViewById(R.id.btnBack)

        // ===== USER ID =====
        userId = intent.getStringExtra("userId")
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbRef = FirebaseDatabase.getInstance()
            .reference
            .child("kondisi_medis")
            .child(userId!!)

        // ===== BACK IMAGE =====
        btnBack.setOnClickListener {
            kembaliKeProfil()
        }

        // ===== BACK HP =====
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                kembaliKeProfil()
            }
        })

        // ===== NEXT =====
        btnNext.setOnClickListener {
            validasiDanSimpan()
        }
    }

    // ================= BACK KE PROFIL =================
    private fun kembaliKeProfil() {
        val intent = Intent(this, ProfilPenggunaActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }

    // ================= VALIDASI =================
    private fun validasiDanSimpan() {
        if (
            rgMenyusui.checkedRadioButtonId == -1 ||
            rgUsiaBayi.checkedRadioButtonId == -1 ||
            rgUsia35.checkedRadioButtonId == -1 ||
            rgPerokok.checkedRadioButtonId == -1
        ) {
            Toast.makeText(this, "Semua pertanyaan wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val menyusui = getRadioText(rgMenyusui)
        val usiaBayi = getRadioText(rgUsiaBayi)
        val usia35 = getRadioText(rgUsia35)
        val perokok = getRadioText(rgPerokok)

        val kondisiTambahan = mutableListOf<String>()
        if (chb1.isChecked) kondisiTambahan.add(chb1.text.toString())
        if (chb2.isChecked) kondisiTambahan.add(chb2.text.toString())
        if (chb3.isChecked) kondisiTambahan.add(chb3.text.toString())
        if (chb4.isChecked) kondisiTambahan.add(chb4.text.toString())
        if (chb5.isChecked) kondisiTambahan.add(chb5.text.toString())
        if (chb6.isChecked) kondisiTambahan.add(chb6.text.toString())

        simpanKeFirebase(
            menyusui,
            usiaBayi,
            usia35,
            perokok,
            kondisiTambahan
        )
    }

    // ================= SIMPAN =================
    private fun simpanKeFirebase(
        menyusui: String,
        usiaBayi: String,
        usia35: String,
        perokok: String,
        kondisiTambahan: List<String>
    ) {
        val dataUpdate = mapOf(
            "menyusui" to menyusui,
            "usia_bayi_kurang_6_bulan" to usiaBayi,
            "usia_diatas_35" to usia35,
            "perokok" to perokok,
            "kondisi_tambahan" to kondisiTambahan,
            "timestamp" to System.currentTimeMillis()
        )

        dbRef.updateChildren(dataUpdate)
            .addOnSuccessListener {
                val intent = Intent(this, preferensi_pengguna::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
            }
    }

    // ================= HELPER =================
    private fun getRadioText(radioGroup: RadioGroup): String {
        return findViewById<RadioButton>(
            radioGroup.checkedRadioButtonId
        ).text.toString()
    }
}
