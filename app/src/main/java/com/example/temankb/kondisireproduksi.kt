package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.view.View
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

    // ===== CARD =====
    private lateinit var cardUsiaBayi: View

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

        cardUsiaBayi = findViewById(R.id.cardQuestion2)

        chb1 = findViewById(R.id.chb1)
        chb2 = findViewById(R.id.chb2)
        chb3 = findViewById(R.id.chb3)
        chb4 = findViewById(R.id.chb4)
        chb5 = findViewById(R.id.chb5)
        chb6 = findViewById(R.id.chb6)

        btnNext = findViewById(R.id.btnReproduksi)
        btnBack = findViewById(R.id.btnBack)

        // ===== DEFAULT =====
        cardUsiaBayi.visibility = View.GONE

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

        // ===== LOGIC MENYUSUI =====
        rgMenyusui.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == -1) return@setOnCheckedChangeListener

            val jawaban = findViewById<RadioButton>(checkedId).text.toString()

            if (jawaban.equals("Ya", true)) {
                cardUsiaBayi.visibility = View.VISIBLE
            } else {
                cardUsiaBayi.visibility = View.GONE
                rgUsiaBayi.clearCheck()
            }
        }

        // ===== BACK =====
        btnBack.setOnClickListener { kembaliKeProfil() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                kembaliKeProfil()
            }
        })

        btnNext.setOnClickListener {
            validasiDanSimpan()
        }
    }

    // ================= VALIDASI =================
    private fun validasiDanSimpan() {

        if (rgMenyusui.checkedRadioButtonId == -1 ||
            rgUsia35.checkedRadioButtonId == -1 ||
            rgPerokok.checkedRadioButtonId == -1
        ) {
            Toast.makeText(this, "Semua pertanyaan wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val menyusui = getRadioText(rgMenyusui)

        if (menyusui.equals("Ya", true) && rgUsiaBayi.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Mohon isi usia bayi", Toast.LENGTH_SHORT).show()
            return
        }

        val kondisi4 = menyusui
        val kondisi5 = if (menyusui.equals("Ya", true)) getRadioText(rgUsiaBayi) else "-"
        val kondisi6 = getRadioText(rgUsia35)
        val kondisi7 = getRadioText(rgPerokok)

        val kondisi8 = mutableListOf<String>()
        if (chb1.isChecked) kondisi8.add(chb1.text.toString())
        if (chb2.isChecked) kondisi8.add(chb2.text.toString())
        if (chb3.isChecked) kondisi8.add(chb3.text.toString())
        if (chb4.isChecked) kondisi8.add(chb4.text.toString())
        if (chb5.isChecked) kondisi8.add(chb5.text.toString())
        if (chb6.isChecked) kondisi8.add(chb6.text.toString())

        simpanKeFirebase(kondisi4, kondisi5, kondisi6, kondisi7, kondisi8)
    }

    // ================= SIMPAN =================
    private fun simpanKeFirebase(
        kondisi4: String,
        kondisi5: String,
        kondisi6: String,
        kondisi7: String,
        kondisi8: List<String>
    ) {
        val dataUpdate = mapOf(
            "kondisi4" to kondisi4,
            "kondisi5" to kondisi5,
            "kondisi6" to kondisi6,
            "kondisi7" to kondisi7,
            "kondisi8" to kondisi8,
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

    private fun kembaliKeProfil() {
        val intent = Intent(this, ProfilPenggunaActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        finish()
    }

    private fun getRadioText(radioGroup: RadioGroup): String {
        return findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            .text.toString()
    }
}
