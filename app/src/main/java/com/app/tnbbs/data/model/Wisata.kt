package com.app.tnbbs.data.model

data class Wisata(
    val namaWisata: String? = null,
    val deskripsi: String? = null,
    val hargaTiket: Int? = null,
    val lokasi: String? = null,
    val kuota: Int? = null,
    val tiketMasukWnaWeekDays: Int? = null,
    val tiketMasukWnaWeekend: Int? = null,
    val tiketMasukWniWeekDays: Int? = null,
    val tiketMasukWniWeekend: Int? = null,
    val imageUrl: String? = null,
    val idWisata: String? = null,
    val generalPrice: Int? = null,
    val groupPrice: Int? = null,
    val hargaKarcisRombonganWnaHariKerja: Int? = null,
    val hargaKarcisRombonganWnaHariLibur: Int? = null,
    val hargaKarcisRombonganWniHariKerja: Int? = null,
    val hargaKarcisRombonganWniHariLibur: Int? = null
)
