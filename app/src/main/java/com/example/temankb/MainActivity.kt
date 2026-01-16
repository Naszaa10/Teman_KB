package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    private val tvTitle by lazy { findViewById<TextView>(R.id.textView) }
    private val tvSubtitle by lazy { findViewById<TextView>(R.id.textView2) }
    private val btnMulaiKonsultasi by lazy { findViewById<AppCompatButton>(R.id.button) }

    private var userName: String? = null
    private var userEmail: String? = null
    private var userId: String? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUserData()
        updateUI()
        setupListeners()
    }

    private fun getUserData() {
        userName = intent.getStringExtra("USER_NAME")
        userEmail = intent.getStringExtra("USER_EMAIL")
        userId = intent.getStringExtra("USER_ID")

        Log.d(TAG, "User data - Name: $userName, Email: $userEmail, ID: $userId")
    }

    private fun updateUI() {
        userName?.let { name ->
            // Update subtitle dengan nama user
            tvSubtitle.text = "Selamat datang, $name!\nPilih alat kontrasepsi yang sesuai untuk Anda"
        }
    }

    private fun setupListeners() {
        // Tombol Mulai Konsultasi
        btnMulaiKonsultasi.setOnClickListener {
            Log.d(TAG, "Tombol Mulai Konsultasi diklik oleh: $userName")

            // TODO: Navigasi ke halaman konsultasi
            // Intent(this, KonsultasiActivity::class.java).apply {
            //     putExtra("USER_ID", userId)
            //     putExtra("USER_NAME", userName)
            //     putExtra("USER_EMAIL", userEmail)
            //     startActivity(this)
            // }
        }

        // Long press pada title untuk logout (hidden feature)
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
        Log.d(TAG, "User $userName melakukan logout")

        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
        finish()
    }

    override fun onBackPressed() {
        // Prevent back to login
        AlertDialog.Builder(this)
            .setTitle("Keluar Aplikasi")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                finishAffinity() // Tutup semua activity
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}