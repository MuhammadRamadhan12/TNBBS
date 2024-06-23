package com.app.tnbbs.view.user.etiket

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.R
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.data.model.Histori
import com.app.tnbbs.databinding.FragmentPembayaranBinding
import com.app.tnbbs.utils.RupiahConverter
import com.app.tnbbs.utils.fixImageOrientation
import com.app.tnbbs.utils.uriToFile
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PembayaranFragment : Fragment() {
    lateinit var binding: FragmentPembayaranBinding
    private lateinit var databaseReference: DatabaseReference
    private var getFileBuktiPembayaran: File? = null
    private var userId = ""
    private lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPembayaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().reference
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
        val totalharga = RupiahConverter.rupiah(bundle?.getInt("totalharga")!!)
        //                putBoolean("isWeekend", isWeekend)
        val isWeekend = bundle?.getBoolean("isWeekend")
        val totalBiayaMasuk = bundle?.getInt("totalBiayaMasuk")

        binding.tvTotalHarga.text = totalharga.toString()

        sharedPref = SharedPref(requireContext())

        lifecycleScope.launch {
            sharedPref.getUserId.collect{
                userId = it
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnUpload.setOnClickListener {
            getBuktiPembayaran()
        }
        binding.btnSalin.setOnClickListener{
            copyToClipboard(it)
        }

        binding.btnKonfirmasi.setOnClickListener {
            getFileBuktiPembayaran?.let { file ->
                saveHistori(file)
            } ?: run {
                Toast.makeText(requireContext(), "Pilih bukti pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun copyToClipboard(view: View) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("nomor rekening", "0198201920192")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Teks berhasil disalin", Toast.LENGTH_SHORT).show()
    }

    private fun getBuktiPembayaran() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(intent)
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri? = result.data?.data
            selectedImg?.let { uri ->
                val myFile = uriToFile(uri, requireContext())
                getFileBuktiPembayaran = fixImageOrientation(myFile, requireContext())
                binding.tvPathFile.text = getFileBuktiPembayaran?.absolutePath
                Log.d(TAG, "Gambar dipilih: $uri")
            }
        } else {
            Log.e(TAG, "Pemilihan gambar gagal")
        }
    }

    private fun saveHistori(selectedImg: File) {
        val storageRef = FirebaseStorage.getInstance().getReference("payment_proofs").child(userId)
        storageRef.putFile(Uri.fromFile(selectedImg))
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        val histori = createHistori(downloadUrl.toString())
                        databaseReference.child("histori").child(userId).push().setValue(histori)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Pembayaran Akan Diproses Oleh Admin", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "Histori disimpan")
                                    findNavController().navigate(
                                        R.id.action_pembayaranFragment_to_homeBerandaFragment,
                                        null,
                                        NavOptions.Builder()
                                            .setPopUpTo(R.id.homeBerandaFragment, true)
                                            .build()
                                    )
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Gagal menyimpan histori: $e")
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Gagal mendapatkan URL unduh. Kesalahan: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Pengunggahan berkas gagal. Kesalahan: ${exception.message}")
            }
    }

    private fun createHistori(proofUrl: String): Histori {

        val currentDate = getCurrentDate()

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
        val totalharga = bundle?.getInt("totalharga")
        val totalBiayaMasuk = bundle?.getInt("totalBiayaMasuk")
        val isWeekends = bundle?.getBoolean("isWeekend")
        return Histori(
            namaWisata,
            deskripsi,
            hargaTiket,
            lokasi,
            kuota,
            tiketMasukWnaWeekDays,
            tiketMasukWnaWeekend,
            tiketMasukWniWeekDays,
            tiketMasukWniWeekend,
            imageUrl,
            idWisata,
            generalPrice,
            groupPrice,
            hargaKarcisRombonganWnaHariKerja,
            hargaKarcisRombonganWnaHariLibur,
            hargaKarcisRombonganWniHariKerja,
            hargaKarcisRombonganWniHariLibur,
            startDate,
            endDate,
            days,
            selectedNationality,
            namaPemesan,
            noHp,
            jumlahOrang,
            totalharga,
            "pending",
            proofUrl,
            currentDate,
            isWeekends,
            totalBiayaMasuk
        )
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }


}
