package com.app.tnbbs.utils

import java.text.NumberFormat
import java.util.Locale

object RupiahConverter {
    // Fungsi untuk mengonversi angka menjadi format mata uang Rupiah
    fun rupiah(number: Int?): String {
        // Membuat objek Locale untuk Indonesia
        val localeID = Locale("in", "ID")
        // Membuat objek NumberFormat untuk format mata uang Indonesia
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        // Mengonversi angka ke dalam format mata uang
        val formattedString = formatRupiah.format(number)
        // Menghapus bagian desimal ",00" dari string yang diformat
        val withoutDecimal = formattedString.replace(",00", "")
        // Mengembalikan string dengan format Rupiah, menghapus simbol mata uang default dan menggantinya dengan "Rp"
        return "Rp${withoutDecimal.substring(2)}"
    }
}
