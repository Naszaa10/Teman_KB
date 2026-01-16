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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        setupListeners()
    }

    private fun setupListeners() {
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
        Log.d(TAG, "Attempting login for email: $email")

        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showLoading(false)
                    Log.d(TAG, "Login query result - user exists: ${snapshot.exists()}")

                    when {
                        !snapshot.exists() -> {
                            showToast("Email tidak terdaftar!")
                            Log.d(TAG, "Email not found in database")
                        }
                        else -> {
                            checkPassword(snapshot, password)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    val errorMsg = "Error: ${error.message}"
                    Log.e(TAG, errorMsg, error.toException())
                    showToast(errorMsg)
                }
            })
    }

    private fun checkPassword(snapshot: DataSnapshot, password: String) {
        snapshot.children.forEach { userSnapshot ->
            val user = userSnapshot.getValue(User::class.java)

            user?.let {
                when {
                    it.password == password -> {
                        Log.d(TAG, "✅ Login successful for user: ${it.name}")
                        showToast("Login berhasil! Selamat datang ${it.name}")
                        navigateToMain(it)
                        return
                    }
                }
            }
        }

        // Jika sampai sini, password salah
        Log.d(TAG, "❌ Invalid password")
        showToast("Password salah!")
    }

    private fun navigateToMain(user: User) {
        Intent(this, MainActivity::class.java).apply {
            putExtra("USER_ID", user.userId)
            putExtra("USER_NAME", user.name)
            putExtra("USER_EMAIL", user.email)
            startActivity(this)
        }
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