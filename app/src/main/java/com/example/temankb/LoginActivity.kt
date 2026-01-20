package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private val etEmail by lazy { findViewById<TextInputEditText>(R.id.etEmail) }
    private val etPassword by lazy { findViewById<TextInputEditText>(R.id.etPassword) }
    private val btnLogin by lazy { findViewById<MaterialButton>(R.id.btnLogin) }
    private val tvRegister by lazy { findViewById<TextView>(R.id.tvRegister) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener { loginUser() }
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = etEmail.text?.toString()?.trim().orEmpty()
        val password = etPassword.text?.toString()?.trim().orEmpty()

        when {
            email.isEmpty() -> {
                etEmail.error = "Email harus diisi"
                etEmail.requestFocus()
                return
            }
            password.isEmpty() -> {
                etPassword.error = "Password harus diisi"
                etPassword.requestFocus()
                return
            }
        }

        performLogin(email, password)
    }

    private fun performLogin(email: String, password: String) {
        showLoading(true)

        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showLoading(false)

                    if (!snapshot.exists()) {
                        showToast("Email tidak terdaftar")
                        return
                    }

                    checkPassword(snapshot, password)
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    showToast(error.message)
                }
            })
    }

    private fun checkPassword(snapshot: DataSnapshot, password: String) {
        for (userSnap in snapshot.children) {
            val user = userSnap.getValue(User::class.java)?.copy(
                userId = userSnap.key   // 🔑 WAJIB
            )

            if (user != null && user.password == password) {
                showToast("Login berhasil, selamat datang ${user.name}")
                navigateToProfil(user)
                return
            }
        }

        showToast("Password salah")
    }

    private fun navigateToProfil(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("EMAIL_LOGIN", user.email)   // 🔑 KONSISTEN
        startActivity(intent)
        finish()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
