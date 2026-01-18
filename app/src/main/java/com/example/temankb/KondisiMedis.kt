package com.example.temankb

data class KondisiMedis(
    val userId: String? = null,
    val nama: String? = null,
    val usia: Int? = null,

    // ProfilPengguna
    val kondisi1: String? = null,
    val kondisi2: String? = null,
    val kondisi3: String? = null,

    // KondisiReproduksi
    val kondisi4: String? = null,
    val kondisi5: String? = null,
    val kondisi6: String? = null,
    val kondisi7: String? = null,
    val kondisi8: List<String>? = null, // checkbox multi-pilih

    // PreferensiPengguna
    val preferensi1: String? = null,
    val preferensi2: String? = null,
    val preferensi3: String? = null,
    val preferensi4: String? = null,

    val timestamp: Long = 0L
) {
    constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0L)
}
