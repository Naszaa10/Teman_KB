package com.example.temankb

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class admin : AppCompatActivity() {

    private lateinit var tvWaktu: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvJumlahUser: TextView
    private lateinit var tvPengunjung: TextView
    private lateinit var spFilter: Spinner

    private val handler = Handler(Looper.getMainLooper())
    private val db = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        tvWaktu = findViewById(R.id.tvWaktu)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvJumlahUser = findViewById(R.id.tvJumlahAkun)
        tvPengunjung = findViewById(R.id.tvPengunjung)
        spFilter = findViewById(R.id.spFilter)

        mulaiJamRealtime()
        setTanggal()
        hitungJumlahUser()

        setupSpinner()
    }

    // ================= JAM REALTIME =================
    private fun mulaiJamRealtime() {
        handler.post(object : Runnable {
            override fun run() {
                val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                tvWaktu.text = format.format(Date())
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun setTanggal() {
        val format = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        tvTanggal.text = format.format(Date())
    }

    // ================= JUMLAH USER =================
    private fun hitungJumlahUser() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalUser = 0
                for (data in snapshot.children) {
                    val role = data.child("role").getValue(String::class.java)
                    if (role == "user") totalUser++
                }
                tvJumlahUser.text = "Akun: $totalUser"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ================= SPINNER =================
    private fun setupSpinner() {
        spFilter.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> hitungPengunjungHariIni()
                        1 -> hitungPengunjung7Hari()
                        2 -> hitungPengunjung30Hari()
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }

    // ================= WAKTU RANGE =================
    private fun startOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun startOfDaysAgo(days: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -days)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // ================= HITUNG PENGUNJUNG =================
    private fun hitungPengunjungHariIni() {
        val start = startOfToday()
        hitungByRange(start)
    }

    private fun hitungPengunjung7Hari() {
        val start = startOfDaysAgo(6) // ⬅️ BEDA DENGAN HARI INI
        hitungByRange(start)
    }

    private fun hitungPengunjung30Hari() {
        val start = startOfDaysAgo(29)
        hitungByRange(start)
    }

    private fun hitungByRange(startTime: Long) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0
                for (data in snapshot.children) {
                    val role = data.child("role").getValue(String::class.java)
                    val lastLogin =
                        data.child("lastLogin").getValue(Long::class.java)

                    if (role == "user" && lastLogin != null && lastLogin >= startTime) {
                        total++
                    }
                }
                tvPengunjung.text = "Pengunjung: $total"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
