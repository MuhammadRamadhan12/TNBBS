package com.app.tnbbs.view.admin.kelolawisata

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.data.model.Wisata
import com.app.tnbbs.databinding.FragmentUpdateWisataBinding
import com.app.tnbbs.utils.fixImageOrientation
import com.app.tnbbs.utils.reduceFileImage
import com.app.tnbbs.utils.uriToFile
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class UpdateWisataFragment : Fragment() {
    lateinit var binding: FragmentUpdateWisataBinding
    private var getFileProfile: File? = null
    private var selectedImg: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateWisataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())
        database = FirebaseDatabase.getInstance().reference

        val idWisata = arguments?.getString("idWisata")

        binding.imageView.setOnClickListener {
            openImagePicker()
        }

        binding.btnSimpan.setOnClickListener {
            updateWisata(idWisata!!)
        }
        binding.btnBack.setOnClickListener{
            activity?.onBackPressed()
        }

        binding.btnBatal.setOnClickListener{
            activity?.onBackPressed()
        }

        if (idWisata != null) {
            fetchWisata(idWisata)
        }
    }

    private fun fetchWisata(idWisata: String) {
        database.child("wisata").child(idWisata).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val wisata = snapshot.getValue(Wisata::class.java)
                    if (wisata != null) {
                        val imageUrl = wisata.imageUrl

                        Glide.with(requireContext())
                            .load(imageUrl)
                            .into(binding.imageView)

                        CoroutineScope(Dispatchers.IO).launch {
                            val localImageFile = downloadImageAndSaveLocally(imageUrl!!)

                            withContext(Dispatchers.Main) {
                                localImageFile?.let { file ->
                                    selectedImg = Uri.fromFile(file)
                                    Glide.with(requireContext())
                                        .load(selectedImg)
                                        .into(binding.imageView)
                                }
                            }
                        }

                        binding.etNamaWisata.setText(wisata.namaWisata)
                        binding.etDeskripsi.setText(wisata.deskripsi)
                        binding.etHargaTiket.setText(wisata.hargaTiket.toString()!!)
                        binding.etLokasi.setText(wisata.lokasi)
                        binding.textViewSelectImage.visibility = View.GONE
                        binding.etKuota.setText(wisata.kuota.toString()!!)
                        binding.etTiketMasukWnaWeekDays.setText(wisata.tiketMasukWnaWeekDays.toString()!!)
                        binding.etTiketMasukWnaWeekend.setText(wisata.tiketMasukWnaWeekend.toString()!!)
                        binding.etTiketMasukWniWeekDays.setText(wisata.tiketMasukWniWeekDays.toString()!!)
                        binding.etTiketMasukWniWeekend.setText(wisata.tiketMasukWniWeekend.toString()!!)
                        binding.etHargaTiketRombongan.setText(wisata.groupPrice.toString()!!)
                        binding.etTiketMasukRombonganWnaWeekDays.setText(wisata.hargaKarcisRombonganWnaHariKerja.toString()!!)
                        binding.etTiketMasukRombonganWnaWeekend.setText(wisata.hargaKarcisRombonganWnaHariLibur.toString()!!)
                        binding.etTiketMasukRombonganWniWeekDays.setText(wisata.hargaKarcisRombonganWniHariKerja.toString()!!)
                        binding.etTiketMasukRombonganWniWeekend.setText(wisata.hargaKarcisRombonganWniHariLibur.toString()!!)

                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }



    private fun downloadImageAndSaveLocally(imageUrl: String): File? {
        val fileName = "wisata${System.currentTimeMillis()}.jpg"
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, fileName)

        try {
            val connection = URL(imageUrl).openConnection() as HttpURLConnection
            connection.connect()

            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
            inputStream.close()

            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(intent)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImg = result.data?.data as Uri
                selectedImg?.let { uri ->
                    val myFile = uriToFile(uri, requireContext())
                    getFileProfile = fixImageOrientation(myFile, requireContext())
                    binding.imageView.setImageURI(uri)
                    binding.textViewSelectImage.visibility = View.GONE
                    Log.d(ContentValues.TAG, "Gambar dipilih: $uri")
                }
            } else {
                Log.e(ContentValues.TAG, "Pemilihan gambar gagal")
            }
        }

    private fun updateWisata(idWisata: String) {
        val namaWisata = binding.etNamaWisata.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()
        val hargaTiket = binding.etHargaTiket.text.toString().trim()
        val hargatiketRombongan = binding.etHargaTiketRombongan.text.toString().trim()
        val lokasi = binding.etLokasi.text.toString().trim()
        val kuota = binding.etKuota.text.toString().trim()
        val tiketMasukWnaWeekDays = binding.etTiketMasukWnaWeekDays.text.toString().trim()
        val tiketMasukWnaWeekend = binding.etTiketMasukWnaWeekend.text.toString().trim()
        val tiketMasukWniWeekDays = binding.etTiketMasukWniWeekDays.text.toString().trim()
        val tiketMasukWniWeekend = binding.etTiketMasukWniWeekend.text.toString().trim()
        val tiketMasukRombonganWnaWeekDays = binding.etTiketMasukRombonganWnaWeekDays.text.toString().trim()
        val tiketMasukRombonganWnaWeekend = binding.etTiketMasukRombonganWnaWeekend.text.toString().trim()
        val tiketMasukRombonganWniWeekDays = binding.etTiketMasukRombonganWniWeekDays.text.toString().trim()
        val tiketMasukRombonganWniWeekend = binding.etTiketMasukRombonganWniWeekend.text.toString().trim()

        if (namaWisata.isEmpty() || deskripsi.isEmpty() || hargaTiket.isEmpty() || lokasi.isEmpty() || kuota.isEmpty() ||
            tiketMasukWnaWeekDays.isEmpty() || tiketMasukWnaWeekend.isEmpty() || tiketMasukWniWeekDays.isEmpty() || tiketMasukWniWeekend.isEmpty() || tiketMasukRombonganWnaWeekDays.isEmpty() || tiketMasukRombonganWnaWeekend.isEmpty() || tiketMasukRombonganWniWeekDays.isEmpty() || tiketMasukRombonganWniWeekend.isEmpty()) {
            Toast.makeText(context, "Semua harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val imageFile = selectedImg?.let { uriToFile(it, requireContext()) }
        val reducedImageFile = imageFile?.let { reduceFileImage(it) }
        val fixedImageFile = reducedImageFile?.let { fixImageOrientation(it, requireContext()) }

        if (fixedImageFile == null) {
            Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}_image.jpg")

        storageRef.putFile(Uri.fromFile(fixedImageFile))
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        val wisataDatabaseRef = FirebaseDatabase.getInstance().getReference("wisata").child(idWisata)

                        val wisataUpdates = hashMapOf<String, Any>(
                            "namaWisata" to namaWisata,
                            "deskripsi" to deskripsi,
                            "hargaTiket" to hargaTiket.toInt(),
                            "lokasi" to lokasi,
                            "kuota" to kuota.toInt(),
                            "tiketMasukWnaWeekDays" to tiketMasukWnaWeekDays.toInt(),
                            "tiketMasukWnaWeekend" to tiketMasukWnaWeekend.toInt(),
                            "tiketMasukWniWeekDays" to tiketMasukWniWeekDays.toInt(),
                            "tiketMasukWniWeekend" to tiketMasukWniWeekend.toInt(),
                            "imageUrl" to downloadUrl.toString(),
                            "generalPrice" to hargaTiket.toInt(),
                            "groupPrice" to hargatiketRombongan.toInt(),
                            "hargaKarcisRombonganWnaHariKerja" to tiketMasukRombonganWnaWeekDays.toInt(),
                            "hargaKarcisRombonganWnaHariLibur" to tiketMasukRombonganWnaWeekend.toInt(),
                            "hargaKarcisRombonganWniHariKerja" to tiketMasukRombonganWniWeekDays.toInt(),
                            "hargaKarcisRombonganWniHariLibur" to tiketMasukRombonganWniWeekend.toInt()
                        )

                        wisataDatabaseRef.updateChildren(wisataUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Wisata berhasil diupdate", Toast.LENGTH_SHORT).show()
                                    findNavController().navigateUp()
                                } else {
                                    Toast.makeText(context, "Gagal mengupdate wisata: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firebase Storage", "Failed to get download URL", exception)
                        Toast.makeText(context, "Gagal mengupdate wisata: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase Storage", "File upload failed", exception)
                Toast.makeText(context, "Gagal mengupdate wisata: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

