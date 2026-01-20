package com.example.temankb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temankb.KondisiMedis
import com.example.temankb.R
import com.example.temankb.adapter.KBAdapter
import com.example.temankb.KBRekomendasiEngine
import com.google.firebase.database.*

class HasilRekomendasiActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasilrekomendasi)

        rv = findViewById(R.id.rvmedis)
        rv.layoutManager = LinearLayoutManager(this)

        userId = intent.getStringExtra("userId")
        dbRef = FirebaseDatabase.getInstance()
            .reference
            .child("kondisi_medis")
            .child(userId!!)

        loadData()
    }

    private fun loadData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(KondisiMedis::class.java)
                if (data != null) {
                    val rekomendasi = KBRekomendasiEngine.getRekomendasi(data)
                    rv.adapter = KBAdapter(rekomendasi)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
