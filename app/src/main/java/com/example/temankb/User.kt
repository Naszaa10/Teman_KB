package com.example.temankb

data class User(
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    var role: String? = "user",
    val registeredAt: Long = 0L
) {
    // Constructor kosong untuk Firebase (required)
    constructor() : this(null, null, null, null, "user" , 0L)
}

