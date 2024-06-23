package com.app.tnbbs.view.user.etiket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.R
import com.app.tnbbs.databinding.FragmentRincianTransaksiBinding
import com.app.tnbbs.utils.RupiahConverter

class RincianTransaksiFragment : Fragment() {
    private lateinit var binding: FragmentRincianTransaksiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRincianTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val bundle = arguments
        val namaWisata = bundle?.getString("namaWisata")
        val deskripsi = bundle?.getString("deskripsi")
        val hargaTiket = bundle?.getString("hargaTiket")
        val lokasi = bundle?.getString("lokasi")
        val kuota = bundle?.getString("kuota")
        val tiketMasukWnaWeekDays = bundle?.getString("tiketMasukWnaWeekDays")
        val tiketMasukWnaWeekend = bundle?.getString("tiketMasukWnaWeekend")
        val tiketMasukWniWeekDays = bundle?.getString("tiketMasukWniWeekDays")
        val tiketMasukWniWeekend = bundle?.getString("tiketMasukWniWeekend")
        val imageUrl = bundle?.getString("imageUrl")
        val idWisata = bundle?.getString("idWisata")
        val generalPrice = bundle?.getString("generalPrice")
        val groupPrice = bundle?.getString("groupPrice")
        val hargaKarcisRombonganWnaHariKerja = bundle?.getString("hargaKarcisRombonganWnaHariKerja")
        val hargaKarcisRombonganWnaHariLibur = bundle?.getString("hargaKarcisRombonganWnaHariLibur")
        val hargaKarcisRombonganWniHariKerja = bundle?.getString("hargaKarcisRombonganWniHariKerja")
        val hargaKarcisRombonganWniHariLibur = bundle?.getString("hargaKarcisRombonganWniHariLibur")
        val startDate = bundle?.getString("startDate")
        val endDate = bundle?.getString("endDate")
        val days = bundle?.getLong("days")
        val selectedNationality = bundle?.getString("selectedNationality")
        val namaPemesan = bundle?.getString("namaPemesan")
        val noHp = bundle?.getString("noHp")
        val jumlahOrang = bundle?.getString("jumlahOrang")
        val totalharga = bundle?.getInt("totalHarga")
        val totalBiayaMasuk = bundle?.getInt("totalBiayaMasuk")
        val isWeekend = bundle?.getBoolean("isWeekend")

        binding.tvNama.text = namaPemesan
        binding.tvHargaTiket.text = "Harga Tiket Wisata : ${RupiahConverter.rupiah(generalPrice?.toInt() ?: 0)}"
        binding.tvEstimasi.text = "Estimasi Hari : ${days?.toString()}"
        binding.tvKewarganegaraan.text = selectedNationality
        binding.tvPhone.text = noHp
        binding.tvJumlahOrang.text = "Jumlah Orang : $jumlahOrang"
        binding.tvTotalHarga.text = RupiahConverter.rupiah(totalharga ?: 0)
        binding.tvHargaTiketMasuk.text = "Harga Tiket Masuk : ${RupiahConverter.rupiah(totalBiayaMasuk)}"

        binding.btnBayar.setOnClickListener{
            val bundleToPass = Bundle().apply {
                putString("namaWisata", namaWisata)
                putString("deskripsi", deskripsi)
                putString("hargaTiket", hargaTiket)
                putString("lokasi", lokasi)
                putString("kuota", kuota)
                putString("tiketMasukWnaWeekDays", tiketMasukWnaWeekDays)
                putString("tiketMasukWnaWeekend", tiketMasukWnaWeekend)
                putString("tiketMasukWniWeekDays", tiketMasukWniWeekDays)
                putString("tiketMasukWniWeekend", tiketMasukWniWeekend)
                putString("imageUrl", imageUrl)
                putString("idWisata", idWisata)
                putString("generalPrice", generalPrice)
                putString("groupPrice", groupPrice)
                putString("hargaKarcisRombonganWnaHariKerja", hargaKarcisRombonganWnaHariKerja)
                putString("hargaKarcisRombonganWnaHariLibur", hargaKarcisRombonganWnaHariLibur)
                putString("hargaKarcisRombonganWniHariKerja", hargaKarcisRombonganWniHariKerja)
                putString("hargaKarcisRombonganWniHariLibur", hargaKarcisRombonganWniHariLibur)
                putString("startDate", startDate)
                putString("endDate", endDate)
                putLong("days", days!!)
                putString("selectedNationality", selectedNationality)
                putString("namaPemesan", namaPemesan)
                putString("noHp", noHp)
                putString("jumlahOrang", jumlahOrang)
                putInt("totalharga", totalharga!!)
                putInt("totalBiayaMasuk", totalBiayaMasuk!!)
                putBoolean("isWeekend", isWeekend!!)
            }
            findNavController().navigate(R.id.action_rincianTransaksiFragment_to_pembayaranFragment, bundleToPass)
        }
    }
}
