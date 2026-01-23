package com.example.temankb

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {

    private val etName by lazy { findViewById<TextInputEditText>(R.id.etName) }
    private val etEmail by lazy { findViewById<TextInputEditText>(R.id.etEmail) }
    private val etPassword by lazy { findViewById<TextInputEditText>(R.id.etPassword) }
    private val etConfirmPassword by lazy { findViewById<TextInputEditText>(R.id.etConfirmPassword) }
    private val btnRegister by lazy { findViewById<MaterialButton>(R.id.btnRegister) }
    private val tvLogin by lazy { findViewById<TextView>(R.id.tvLogin) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private val database = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister.setOnClickListener { registerUser() }
        tvLogin.setOnClickListener { finish() }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Lengkapi semua data")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email tidak valid"
            return
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Password tidak sama"
            return
        }

        showLoading(true)

        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        showLoading(false)
                        showToast("Email sudah terdaftar")
                        return
                    }

                    val userId = database.push().key!!
                    val user = User(
                        userId = userId,
                        name = name,
                        email = email,
                        password = password,
                        role = "user",
                        registeredAt = System.currentTimeMillis()
                    )

                    database.child(userId).setValue(user)
                        .addOnSuccessListener {
                            showLoading(false)
                            showToast("Registrasi berhasil")
                            finish()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    showToast(error.message)
                }
            })
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !show
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}