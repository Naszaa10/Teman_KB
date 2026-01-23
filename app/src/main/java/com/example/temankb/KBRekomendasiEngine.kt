package com.example.temankb

import com.example.temankb.model.KBItem

object KBRekomendasiEngine {

    fun getRekomendasi(data: KondisiMedis): List<KBItem> {

        val hasil = mutableListOf<KBItem>()

        // =====================
        // DATA DASAR
        // =====================
        val hamil = data.kondisi1 == "Ya"
        val kankerPayudara = data.kondisi2 == "Ya"
        val perdarahan = data.kondisi3 == "Ya"
        val menyusui = data.kondisi4 == "Ya"
        val bayiKurang6Bulan = data.kondisi5 == "Ya"
        val asiEksklusif = menyusui && bayiKurang6Bulan // ASI eksklusif < 6 bulan
        val usia35 = data.kondisi6 == "Ya"
        val perokok = data.kondisi7 == "Ya"

        val jangkaPanjang = data.preferensi1 == "Ya"
        val lupaPil = data.preferensi2 == "Ya"
        val butuhIMS = data.preferensi3 == "Ya"
        val nyamanAlat = data.preferensi4 == "Ya"

        val kondisiBerat = listOf(
            "Tekanan darah tinggi berat (>180/110)",
            "Stroke",
            "Penyakit jantung",
            "Gangguan pembekuan darah"
        )

        val kontraEstrogen = listOf(
            "Hipertensi",
            "Migrain",
            "Diabetes > 20 tahun",
            "Epilepsi",
            "Tuberkulosis"
        )

        // Kondisi kontra untuk AKDR (dari modul)
        val kontraAKDR = listOf(
            "Peradangan panggul",
            "Mioma uteri submukosa",
            "Dismenore berat",
            "Karsinoma organ panggul",
            "Malformasi rahim"
        )

        val adaKondisiBerat =
            data.kondisi8?.any { it in kondisiBerat } == true

        val adaKontraEstrogen =
            data.kondisi8?.any { it in kontraEstrogen } == true

        val adaKontraAKDR =
            data.kondisi8?.any { it in kontraAKDR } == true

        // =====================
        // STOP RULE ABSOLUT
        // =====================
        if (hamil) {
            return listOf(
                KBItem(
                    "Tidak Ada KB",
                    "Sedang hamil - semua metode KB dikontraindikasikan"
                )
            )
        }

        if (perdarahan) {
            return listOf(
                KBItem(
                    "Konsultasi Medis",
                    "Perdarahan pervagina perlu evaluasi medis terlebih dahulu"
                )
            )
        }

        // =====================
        // KANKER PAYUDARA
        // =====================
        if (kankerPayudara) {
            // Kanker payudara = KONTRA MUTLAK semua hormonal
            if (!adaKontraAKDR) {
                return listOf(
                    KBItem("AKDR/IUD Non-Hormonal", "Pilihan terbaik - non-hormonal, efektif 4-10 tahun"),
                    KBItem("Kondom", "Proteksi ganda terhadap IMS")
                )
            } else {
                return listOf(
                    KBItem("Kondom", "Satu-satunya pilihan aman untuk kondisi Anda")
                )
            }
        }

        // =====================
        // ATURAN MEDIS
        // =====================
        // Estrogen DILARANG untuk:
        val estrogenDilarang =
            asiEksklusif || // ASI eksklusif (menyusui + bayi < 6 bulan)
                    (usia35 && perokok) || // Risiko kardiovaskular tinggi
                    adaKontraEstrogen

        // Hanya boleh progestin atau non-hormonal:
        val hanyaProgestinAtauNonHormonal = adaKondisiBerat

        // =====================
        // REKOMENDASI UTAMA
        // =====================

        // PRIORITAS 1: IMS Protection (SELALU PERTAMA jika butuh)
        if (butuhIMS) {
            hasil.add(
                KBItem("Kondom", "Satu-satunya yang melindungi dari IMS/HIV/AIDS")
            )
        }

        // PRIORITAS 2: AKDR (Jangka panjang, non-hormonal, efektif tinggi)
        if (jangkaPanjang && !adaKontraAKDR) {
            hasil.add(
                KBItem("AKDR/IUD", "Efektif 4-10 tahun (>99%), non-hormonal, cocok untuk semua termasuk menyusui")
            )
        }

        // PRIORITAS 3: Implan (Jangka panjang, progestin saja)
        if (jangkaPanjang) {
            // Implan SELALU cocok untuk menyusui (hanya progestin)
            hasil.add(
                KBItem("Implan", "Efektif 3-5 tahun (>99%), hanya progestin, aman untuk menyusui")
            )
        }

        // PRIORITAS 4: Suntikan Progestin (Cocok untuk yang lupa pil, menyusui)
        if (lupaPil || hanyaProgestinAtauNonHormonal || menyusui) {
            hasil.add(
                KBItem("Suntikan Progestin", "Setiap 3 bulan, aman untuk menyusui, cocok yang sering lupa pil")
            )
        }

        // PRIORITAS 5: Suntikan Kombinasi
        // SYARAT: TIDAK ASI eksklusif, TIDAK kondisi berat, TIDAK kontra estrogen
        if (!estrogenDilarang && !hanyaProgestinAtauNonHormonal && lupaPil) {
            // Boleh untuk: menyusui >6 bulan, atau tidak menyusui
            hasil.add(
                KBItem("Suntikan Kombinasi", "Setiap bulan, mengatur siklus haid, cocok yang lupa pil")
            )
        }

        // PRIORITAS 6: Pil Kombinasi
        // SYARAT: TIDAK ASI eksklusif, TIDAK lupa pil, TIDAK kondisi berat
        if (!estrogenDilarang && !lupaPil && !hanyaProgestinAtauNonHormonal) {
            // "Tidak dianjurkan" untuk menyusui, tapi BUKAN dilarang mutlak
            // Boleh untuk: menyusui >6 bulan (tidak ASI eksklusif), atau tidak menyusui
            if (menyusui) {
                hasil.add(
                    KBItem("Pil Kombinasi", "Diminum teratur tiap hari, kurang dianjurkan saat menyusui")
                )
            } else {
                hasil.add(
                    KBItem("Pil Kombinasi", "Efektif 99% bila diminum teratur tiap hari, mengatur siklus haid")
                )
            }
        }

        // PRIORITAS 7: Mini Pil (Progestin saja)
        // COCOK untuk: menyusui, kontra estrogen, TAPI harus disiplin tinggi
        if (!lupaPil) {
            if (estrogenDilarang || menyusui) {
                hasil.add(
                    KBItem("Mini Pil", "Aman untuk menyusui, HARUS diminum jam sama tiap hari (telat 3 jam = risiko hamil)")
                )
            }
        }

        // FALLBACK: Jika tidak ada rekomendasi sama sekali
        if (hasil.isEmpty()) {
            hasil.add(
                KBItem("Kondom", "Metode paling aman dan fleksibel untuk kondisi Anda")
            )
            // Tambahkan konsultasi
            hasil.add(
                KBItem("Konsultasi Medis", "Sebaiknya konsultasi dengan tenaga kesehatan untuk kondisi khusus Anda")
            )
        }

        // Return maksimal 3 rekomendasi teratas
        return hasil.distinctBy { it.nama }.take(3)
    }
}