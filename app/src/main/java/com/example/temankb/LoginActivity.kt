package com.example.temankb

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)

        setupRegisterText()

        btnLogin.setOnClickListener { loginUser() }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // =============================
    // TEXT "DAFTAR DISINI" BIRU
    // =============================
    private fun setupRegisterText() {
        val text = "Belum punya akun? Daftar disini"
        val spannable = SpannableString(text)

        val start = text.indexOf("Daftar disini")
        val end = start + "Daftar disini".length

        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.klik_register)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            UnderlineSpan(),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvRegister.text = spannable
    }

    // =============================
    // LOGIN USER
    // =============================
    private fun loginUser() {
        val email = etEmail.text.toString().trim().lowercase()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Email dan password wajib diisi")
            return
        }

        showLoading(true)

        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    showLoading(false)

                    if (!snapshot.exists()) {
                        showToast("Email tidak terdaftar")
                        return
                    }

                    for (data in snapshot.children) {
                        val user = data.getValue(User::class.java)

                        if (user != null && user.password == password) {

                            // =============================
                            // CATAT LOGIN HARIAN (HANYA USER)
                            // =============================
                            if (user.role == "user") {
                                database.child(data.key!!)
                                    .child("lastLogin")
                                    .setValue(System.currentTimeMillis())
                            }

                            val intent = if (user.role == "admin") {
                                Intent(this@LoginActivity, admin::class.java)
                            } else {
                                Intent(this@LoginActivity, MainActivity::class.java)
                            }

                            intent.putExtra("EMAIL_LOGIN", user.email)

                            startActivity(intent)
                            finish()
                            return
                        }
                    }

                    showToast("Password salah")
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    showToast(error.message)
                }
            })
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !show
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
