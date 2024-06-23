package com.app.tnbbs.view.user.etiket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.R
import com.app.tnbbs.databinding.FragmentInputTransaksiBinding
import com.google.android.material.datepicker.MaterialDatePicker
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class InputTransaksiFragment : Fragment() {
    private lateinit var binding: FragmentInputTransaksiBinding
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
    private var days: Long? = null
    private var selectedNationality: String? = null
    private var totalHarga: Int = 0
    private var totalBiayaMasuk: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInputTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.etDate.setOnClickListener {
            showDateRangePicker()
        }

        binding.radioGroupNationality.setOnCheckedChangeListener { _, checkedId ->
            selectedNationality = when (checkedId) {
                R.id.radioButtonWNI -> "WNI"
                R.id.radioButtonWNA -> "WNA"
                else -> null
            }
            Log.d("selectedNationality", selectedNationality.toString())
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

        Log.d("BundleData", "namaWisata: $namaWisata, deskripsi: $deskripsi, hargaTiket: $hargaTiket, lokasi: $lokasi, kuota: $kuota")
        Log.d("BundleData", "tiketMasukWnaWeekDays: $tiketMasukWnaWeekDays, tiketMasukWnaWeekend: $tiketMasukWnaWeekend, tiketMasukWniWeekDays: $tiketMasukWniWeekDays, tiketMasukWniWeekend: $tiketMasukWniWeekend")
        Log.d("BundleData", "imageUrl: $imageUrl, idWisata: $idWisata, generalPrice: $generalPrice, groupPrice: $groupPrice")
        Log.d("BundleData", "hargaKarcisRombonganWnaHariKerja: $hargaKarcisRombonganWnaHariKerja, hargaKarcisRombonganWnaHariLibur: $hargaKarcisRombonganWnaHariLibur, hargaKarcisRombonganWniHariKerja: $hargaKarcisRombonganWniHariKerja, hargaKarcisRombonganWniHariLibur: $hargaKarcisRombonganWniHariLibur")

        binding.tvTiket.text = namaWisata

        binding.btnPesan.setOnClickListener {
            val namaPemesan = binding.etNamaPemesan.text.toString()
            val noHp = binding.etNoTelepon.text.toString()
            val jumlahOrang = binding.etJumlah.text.toString().toIntOrNull() ?: 0

            if (namaPemesan.isEmpty() || noHp.isEmpty() || jumlahOrang <= 0 || startDate == null || endDate == null || selectedNationality == null) {
                Toast.makeText(requireContext(), "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hargaTiketYangDipakai = determineTicketPrice(
                startDate,
                endDate,
                selectedNationality,
                jumlahOrang,
                tiketMasukWnaWeekDays!!.toInt(),
                tiketMasukWnaWeekend!!.toInt(),
                tiketMasukWniWeekDays!!.toInt(),
                tiketMasukWniWeekend!!.toInt(),
                hargaKarcisRombonganWnaHariKerja!!.toInt(),
                hargaKarcisRombonganWnaHariLibur!!.toInt(),
                hargaKarcisRombonganWniHariKerja!!.toInt(),
                hargaKarcisRombonganWniHariLibur!!.toInt(),
                generalPrice!!.toInt(),
                groupPrice!!.toInt()
            )

            val startDayOfWeek = startDate?.dayOfWeek
            val endDayOfWeek = endDate?.dayOfWeek
            val isWeekend = startDayOfWeek == DayOfWeek.SATURDAY || startDayOfWeek == DayOfWeek.SUNDAY ||
                    endDayOfWeek == DayOfWeek.SATURDAY || endDayOfWeek == DayOfWeek.SUNDAY

            val bundleToPass = Bundle().apply {
                putString("namaWisata", namaWisata)
                putString("deskripsi", deskripsi)
                putString("hargaTiket", hargaTiketYangDipakai.toString())
                putString("lokasi", lokasi)
                putString("kuota", (kuota ?: 0).toString())
                putString("tiketMasukWnaWeekDays", (tiketMasukWnaWeekDays ?: 0).toString())
                putString("tiketMasukWnaWeekend", (tiketMasukWnaWeekend ?: 0).toString())
                putString("tiketMasukWniWeekDays", (tiketMasukWniWeekDays ?: 0).toString())
                putString("tiketMasukWniWeekend", (tiketMasukWniWeekend ?: 0).toString())
                putString("imageUrl", imageUrl)
                putString("idWisata", idWisata)
                putString("generalPrice", (generalPrice ?: 0).toString())
                putString("groupPrice", (groupPrice ?: 0).toString())
                putString("hargaKarcisRombonganWnaHariKerja", (hargaKarcisRombonganWnaHariKerja ?: 0).toString())
                putString("hargaKarcisRombonganWnaHariLibur", (hargaKarcisRombonganWnaHariLibur ?: 0).toString())
                putString("hargaKarcisRombonganWniHariKerja", (hargaKarcisRombonganWniHariKerja ?: 0).toString())
                putString("hargaKarcisRombonganWniHariLibur", (hargaKarcisRombonganWniHariLibur ?: 0).toString())
                putString("startDate", startDate.toString())
                putString("endDate", endDate.toString())
                putLong("days", days ?: 0L)
                putString("selectedNationality", selectedNationality)
                putString("namaPemesan", namaPemesan)
                putString("noHp", noHp)
                putString("jumlahOrang", jumlahOrang.toString())
                putInt("totalHarga", totalHarga)
                putInt("totalBiayaMasuk", totalBiayaMasuk)
                putBoolean("isWeekend", isWeekend)
            }
            findNavController().navigate(R.id.action_inputTransaksiFragment_to_rincianTransaksiFragment, bundleToPass)
        }
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection ->
            if (selection.first != null && selection.second != null) {
                val startMillis = selection.first
                val endMillis = selection.second

                startDate = LocalDate.ofEpochDay(startMillis / 86400000)
                endDate = LocalDate.ofEpochDay(endMillis / 86400000)

                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                binding.etDate.setText(
                    "${startDate?.format(formatter)} - ${endDate?.format(formatter)}"
                )

                days = endDate?.toEpochDay()?.minus(startDate?.toEpochDay()!!)?.plus(1)
                Log.d("days", days.toString())

                val startDayOfWeek = startDate?.dayOfWeek
                val endDayOfWeek = endDate?.dayOfWeek
                val isWeekend = startDayOfWeek == DayOfWeek.SATURDAY || startDayOfWeek == DayOfWeek.SUNDAY ||
                        endDayOfWeek == DayOfWeek.SATURDAY || endDayOfWeek == DayOfWeek.SUNDAY
                Log.d("isWeekend", isWeekend.toString())
            } else {
                // TODO
            }
        }
        picker.show(parentFragmentManager, picker.toString())
    }

    private fun determineTicketPrice(
        startDate: LocalDate?,
        endDate: LocalDate?,
        nationality: String?,
        jumlahOrang: Int,
        wnaWeekdays: Int,
        wnaWeekend: Int,
        wniWeekdays: Int,
        wniWeekend: Int,
        hargaKarcisRombonganWnaHariKerja: Int,
        hargaKarcisRombonganWnaHariLibur: Int,
        hargaKarcisRombonganWniHariKerja: Int,
        hargaKarcisRombonganWniHariLibur: Int,
        generalPrice: Int,
        groupPrice: Int
    ): Int {
        val startDayOfWeek = startDate?.dayOfWeek
        val endDayOfWeek = endDate?.dayOfWeek

        val isWeekend = when (startDayOfWeek) {
            DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> true
            else -> when (endDayOfWeek) {
                DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> true
                else -> false
            }
        }

        val isGroup = jumlahOrang >= 10

        val tiketMasuk = when (nationality) {
            "WNA" -> if (isWeekend) wnaWeekend else wnaWeekdays
            "WNI" -> if (isWeekend) wniWeekend else wniWeekdays
            else -> 0
        }

        val hargaKarcisRombongan = when (nationality) {
            "WNA" -> if (isWeekend) hargaKarcisRombonganWnaHariLibur else hargaKarcisRombonganWnaHariKerja
            "WNI" -> if (isWeekend) hargaKarcisRombonganWniHariLibur else hargaKarcisRombonganWniHariKerja
            else -> 0
        }

        val hargaTiketPerHari = if (isGroup) groupPrice else generalPrice

        val totalHari = days?.toInt() ?: 1

        val biayaMasuk = if (isGroup) hargaKarcisRombongan else tiketMasuk

        val totalBiayaWisata = (hargaTiketPerHari + biayaMasuk) * totalHari * jumlahOrang

        totalHarga = totalBiayaWisata
        totalBiayaMasuk = biayaMasuk

        Log.d("determineTicketPrice", "Start Date: $startDate")
        Log.d("determineTicketPrice", "End Date: $endDate")
        Log.d("determineTicketPrice", "Nationality: $nationality")
        Log.d("determineTicketPrice", "Number of People: $jumlahOrang")
        Log.d("determineTicketPrice", "Is Weekend: $isWeekend")
        Log.d("determineTicketPrice", "Is Group: $isGroup")
        Log.d("determineTicketPrice", "Ticket Price Per Day: $hargaTiketPerHari")
        Log.d("determineTicketPrice", "Total Days: $totalHari")
        Log.d("determineTicketPrice", "Total Biaya Wisata: $totalBiayaWisata")
        Log.d("determineTicketPrice", "Total Biaya Masuk: $biayaMasuk")

        return totalBiayaWisata
    }
}
