package com.example.temankb

import com.example.temankb.model.KBItem

object KBRekomendasiEngine {

    fun getRekomendasi(data: KondisiMedis): List<KBItem> {

        val hasil = mutableListOf<KBItem>()

        val hamil = data.kondisi1 == "Ya"
        val kankerPayudara = data.kondisi2 == "Ya"
        val perdarahan = data.kondisi3 == "Ya"
        val menyusui = data.kondisi4 == "Ya"
        val bayiKurang6Bulan = data.kondisi5 == "Ya"
        val usia35 = data.kondisi6 == "Ya"
        val perokok = data.kondisi7 == "Ya"

        val jangkaPanjang = data.preferensi1 == "Ya"
        val lupaPil = data.preferensi2 == "Ya"
        val butuhIMS = data.preferensi3 == "Ya"
        val nyamanAlat = data.preferensi4 == "Ya"

        // =====================
        // KONDISI 8 (CHECKBOX)
        // =====================
        val kontraEstrogen = listOf(
            "Hipertensi",
            "Diabetes > 20 tahun",
            "Migrain",
            "Epilepsi",
            "Tuberkulosis",
            "Gangguan pembekuan darah",
            "Stroke",
            "Penyakit jantung"
        )

        val adaKontraEstrogen =
            data.kondisi8?.any { it in kontraEstrogen } ?: false

        // =====================
        // STOP RULE (ABSOLUT)
        // =====================
        if (hamil) {
            return listOf(
                KBItem("Kondom", "Aman digunakan saat hamil")
            )
        }

        if (perdarahan) {
            return listOf(
                KBItem(
                    "Tunda Kontrasepsi",
                    "Perdarahan pervaginam perlu evaluasi medis"
                )
            )
        }

        if (kankerPayudara) {
            hasil.add(KBItem("AKDR / IUD", "Non hormonal aman"))
            if (butuhIMS) hasil.add(KBItem("Kondom", "Perlindungan IMS"))
            return hasil
        }

        // =====================
        // ESTROGEN BOLEH?
        // =====================
        val estrogenBoleh =
            (!menyusui || !bayiKurang6Bulan) &&
                    !(usia35 && perokok) &&
                    !adaKontraEstrogen

        // =====================
        // JANGKA PANJANG
        // =====================
        if (jangkaPanjang && nyamanAlat) {
            hasil.add(
                KBItem("AKDR / IUD", "Kontrasepsi jangka panjang non hormonal")
            )
            if (!adaKontraEstrogen) {
                hasil.add(
                    KBItem("Implan", "Alternatif jangka panjang hormonal")
                )
            }
        }

        // =====================
        // JANGKA PENDEK
        // =====================
        if (lupaPil) {
            hasil.add(
                KBItem("Suntikan", "Cocok bila sering lupa pil")
            )
        }

        if (estrogenBoleh && !lupaPil) {
            hasil.add(
                KBItem("Pil Kombinasi", "Efektif dan teratur")
            )
        }

        if (!estrogenBoleh) {
            hasil.add(
                KBItem("Mini Pil", "Pil tanpa estrogen")
            )
        }

        // =====================
        // IMS
        // =====================
        if (butuhIMS) {
            hasil.add(
                KBItem("Kondom", "Perlindungan IMS/HIV")
            )
        }

        return hasil.distinctBy { it.nama }.take(2)
    }
}
