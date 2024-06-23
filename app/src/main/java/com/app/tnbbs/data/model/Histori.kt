package com.app.tnbbs.data.model

data class Histori(
    val namaWisata: String? = null,
    val deskripsi: String? = null,
    val hargaTiket: String? = null,
    val lokasi: String? = null,
    val kuota: String? = null,
    val tiketMasukWnaWeekDays: String? = null,
    val tiketMasukWnaWeekend: String? = null,
    val tiketMasukWniWeekDays: String? = null,
    val tiketMasukWniWeekend: String? = null,
    val imageUrl: String? = null,
    val idWisata: String? = null,
    val generalPrice: String? = null,
    val groupPrice: String? = null,
    val hargaKarcisRombonganWnaHariKerja: String? = null,
    val hargaKarcisRombonganWnaHariLibur: String? = null,
    val hargaKarcisRombonganWniHariKerja: String? = null,
    val hargaKarcisRombonganWniHariLibur: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val days: Long? = null,
    val selectedNationality: String? = null,
    val namaPemesan: String? = null,
    val noHp: String? = null,
    val jumlahOrang: String? = null,
    val totalharga: Int? = null,
    val statusPesanan: String? = null,
    val buktiPembayaran: String? = null,
    val timestamp: String? = null,
    var isWeekend: Boolean? = null,
    var totalBiayaMasuk: Int? = null,
    var idBooking: String? = null
)
