package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var btnMulaiKonsultasi: AppCompatButton

    private var userId: String? = null
    private var userName: String? = null
    private var userEmail: String? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTitle = findViewById(R.id.textView)
        tvSubtitle = findViewById(R.id.textView2)
        btnMulaiKonsultasi = findViewById(R.id.btnNextPage2)

        // 🔑 Ambil email dari LoginActivity
        userEmail = intent.getStringExtra("EMAIL_LOGIN")

        if (userEmail == null) {
            Log.e(TAG, "Email login tidak ditemukan")
            finish()
            return
        }

        ambilUserDariFirebase(userEmail!!)
        setupListeners()
    }

    // 🔥 Ambil nama user dari Firebase berdasarkan email
    private fun ambilUserDariFirebase(email: String) {
        val db = FirebaseDatabase.getInstance().reference.child("users")

        db.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Log.e(TAG, "User tidak ditemukan untuk email: $email")
                        return
                    }

                    for (userSnap in snapshot.children) {
                        userId = userSnap.key
                        userName = userSnap.child("name").getValue(String::class.java)
                        userEmail = userSnap.child("email").getValue(String::class.java)

                        Log.d(
                            TAG,
                            "User ditemukan -> ID: $userId, Name: $userName, Email: $userEmail"
                        )

                        updateUI()
                        break
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Firebase error: ${error.message}")
                }
            })
    }

    private fun updateUI() {
        tvSubtitle.text =
            "Selamat datang, ${userName ?: "Pengguna"}!\n" +
                    "Pilih alat kontrasepsi yang sesuai untuk Anda"
    }

    private fun setupListeners() {

        // ➡️ Ke halaman Tentang KB
        btnMulaiKonsultasi.setOnClickListener {
            val intent = Intent(this, tentangkb::class.java)
            intent.putExtra("EMAIL_LOGIN", userEmail)
            startActivity(intent)
        }

        // 🔙 Long press title untuk logout
        tvTitle.setOnLongClickListener {
            showLogoutDialog()
            true
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Keluar dari akun $userName?")
            .setPositiveButton("Ya") { _, _ ->
                logout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}