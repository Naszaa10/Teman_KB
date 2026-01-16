package com.example.temankb

import android.os.Bundle
import android.util.Log
import android.util.Patterns
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

class RegisterActivity : AppCompatActivity() {

    private val etName by lazy { findViewById<TextInputEditText>(R.id.etName) }
    private val etEmail by lazy { findViewById<TextInputEditText>(R.id.etEmail) }
    private val etPassword by lazy { findViewById<TextInputEditText>(R.id.etPassword) }
    private val etConfirmPassword by lazy { findViewById<TextInputEditText>(R.id.etConfirmPassword) }
    private val btnRegister by lazy { findViewById<MaterialButton>(R.id.btnRegister) }
    private val tvLogin by lazy { findViewById<TextView>(R.id.tvLogin) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("users")
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupListeners()
        testFirebaseConnection()
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener { registerUser() }
        tvLogin.setOnClickListener { finish() }
    }

    private fun testFirebaseConnection() {
        database.parent?.child("test")?.setValue("connection_test")
            ?.addOnSuccessListener {
                Log.d(TAG, "✅ Firebase connected successfully!")
            }
            ?.addOnFailureListener { e ->
                Log.e(TAG, "❌ Firebase connection failed: ${e.message}")
                showToast("Koneksi Firebase gagal: ${e.message}")
            }
    }

    private fun registerUser() {
        val name = etName.text?.toString()?.trim().orEmpty()
        val email = etEmail.text?.toString()?.trim().orEmpty()
        val password = etPassword.text?.toString()?.trim().orEmpty()
        val confirmPassword = etConfirmPassword.text?.toString()?.trim().orEmpty()

        when {
            name.isEmpty() -> {
                etName.error = "Nama harus diisi"
                etName.requestFocus()
                return
            }
            email.isEmpty() -> {
                etEmail.error = "Email harus diisi"
                etEmail.requestFocus()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Format email tidak valid"
                etEmail.requestFocus()
                return
            }
            password.isEmpty() -> {
                etPassword.error = "Password harus diisi"
                etPassword.requestFocus()
                return
            }
            password.length < 6 -> {
                etPassword.error = "Password minimal 6 karakter"
                etPassword.requestFocus()
                return
            }
            confirmPassword.isEmpty() -> {
                etConfirmPassword.error = "Konfirmasi password harus diisi"
                etConfirmPassword.requestFocus()
                return
            }
            password != confirmPassword -> {
                etConfirmPassword.error = "Password tidak sama"
                etConfirmPassword.requestFocus()
                return
            }
        }

        checkEmailAndRegister(name, email, password)
    }

    private fun checkEmailAndRegister(name: String, email: String, password: String) {
        showLoading(true)
        Log.d(TAG, "Checking email: $email")

        database.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "Email check result - exists: ${snapshot.exists()}")

                    when {
                        snapshot.exists() -> {
                            showLoading(false)
                            showToast("Email sudah terdaftar!")
                            Log.d(TAG, "Email already registered")
                        }
                        else -> {
                            Log.d(TAG, "Email available, proceeding with registration")
                            saveUserToDatabase(name, email, password)
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

    private fun saveUserToDatabase(name: String, email: String, password: String) {
        val userId = database.push().key

        if (userId == null) {
            showLoading(false)
            showToast("Error membuat user ID")
            Log.e(TAG, "Generated userId is null")
            return
        }

        Log.d(TAG, "Generated userId: $userId")

        val user = User(
            userId = userId,
            name = name,
            email = email,
            password = password,
            registeredAt = System.currentTimeMillis()
        )

        Log.d(TAG, "Attempting to save user: $user")

        database.child(userId).setValue(user)
            .addOnSuccessListener {
                showLoading(false)
                Log.d(TAG, "✅ User registered successfully!")
                showToast("Registrasi berhasil! Silakan login")
                finish()
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                val errorMsg = "Registrasi gagal: ${exception.message}"
                Log.e(TAG, errorMsg, exception)
                showToast(errorMsg)
            }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !show
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}