package com.example.temankb

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temankb.adapter.KBAdapter
import com.google.firebase.database.*

class HasilRekomendasiActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var tvNama: TextView
    private lateinit var tvUmur: TextView
    private lateinit var tvRiwayat: TextView

    private lateinit var dbRef: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasilrekomendasi)

        // ==== VIEW ====
        rv = findViewById(R.id.rvmedis)
        tvNama = findViewById(R.id.tvNama)
        tvUmur = findViewById(R.id.tvUmur)
        tvRiwayat = findViewById(R.id.tvRiwayatMedis)

        rv.layoutManager = LinearLayoutManager(this)

        // ==== USER ID ====
        userId = intent.getStringExtra("userId")
        if (userId.isNullOrEmpty()) {
            finish()
            return
        }

        // ==== FIREBASE ====
        dbRef = FirebaseDatabase.getInstance()
            .reference
            .child("kondisi_medis")
            .child(userId!!)

        loadData()
    }

    // =========================
    // LOAD SEMUA DATA
    // =========================
    private fun loadData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val data = snapshot.getValue(KondisiMedis::class.java)
                if (data == null) return

                // ===== PROFIL =====
                tvNama.text = "Nama : ${data.nama ?: "-"}"
                tvUmur.text = "Umur : ${data.usia ?: "-"} tahun"

                // ===== RIWAYAT MEDIS =====
                tvRiwayat.text = buildRiwayatMedis(data)

                // ===== REKOMENDASI =====
                val rekomendasi = KBRekomendasiEngine.getRekomendasi(data)
                rv.adapter = KBAdapter(rekomendasi)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // =========================
    // FORMAT RIWAYAT MEDIS
    // =========================
    private fun buildRiwayatMedis(data: KondisiMedis): String {
        val sb = StringBuilder()

        sb.append("Hamil : ${data.kondisi1}\n")
        sb.append("Kanker Payudara : ${data.kondisi2}\n")
        sb.append("Perdarahan : ${data.kondisi3}\n")
        sb.append("Menyusui : ${data.kondisi4}\n")
        sb.append("Bayi < 6 bulan : ${data.kondisi5}\n")
        sb.append("Usia > 35 tahun : ${data.kondisi6}\n")
        sb.append("Perokok : ${data.kondisi7}\n")

        if (!data.kondisi8.isNullOrEmpty()) {
            sb.append("\nPenyakit Penyerta:\n")
            data.kondisi8.forEach {
                sb.append("- $it\n")
            }
        }

        sb.append("\nPreferensi:\n")
        sb.append("KB jangka panjang : ${data.preferensi1}\n")
        sb.append("Sering lupa pil : ${data.preferensi2}\n")
        sb.append("Butuh perlindungan IMS : ${data.preferensi3}\n")
        sb.append("Nyaman AKDR/Implan : ${data.preferensi4}\n")

        return sb.toString()
    }
}
