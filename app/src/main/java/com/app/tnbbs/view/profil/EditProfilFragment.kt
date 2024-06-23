package com.app.tnbbs.view.profil

import android.app.Activity
import android.content.ContentValues.TAG
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
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.data.datastore.SharedPref
import com.app.tnbbs.databinding.FragmentEditProfilBinding
import com.app.tnbbs.utils.fixImageOrientation
import com.app.tnbbs.utils.reduceFileImage
import com.app.tnbbs.utils.uriToFile
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class EditProfilFragment : Fragment() {
    private lateinit var binding: FragmentEditProfilBinding
    private lateinit var auth: FirebaseAuth
    private var getFileProfile: File? = null
    private var selectedImg: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var sharedPref: SharedPref
    var userId : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.imgProfil.setOnClickListener {
            openImagePicker()
        }

        binding.btnSimpan.setOnClickListener {
            doUpdate()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        lifecycleScope.launch {
            sharedPref.getUserId.asLiveData().observe(viewLifecycleOwner) {
                userId = it
            }
        }

        getProfile()
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
                    binding.imgProfil.setImageURI(uri)
                    Log.d(TAG, "Gambar dipilih: $uri")
                }
            } else {
                Log.e(TAG, "Pemilihan gambar gagal")
            }
        }

    private fun doUpdate() {
        if (selectedImg != null) {
            val id = userId
            val emailBaru = binding.etEmail.text.toString()
            val kataSandiBaru = binding.editPassword.text.toString()
            val konfirmasiKataSandi = binding.editConfirmPassword.text.toString()
            val usernameBaru = binding.etUsername.text.toString()
            val namaDepanBaru = binding.etNamaDepan.text.toString()
            val namaBelakangBaru = binding.etNamaBelakang.text.toString()
            val noTeleponBaru = binding.etPhone.text.toString()
            val kataSandiSaatIni = binding.editConfirmPassword.text.toString()

            if (emailBaru.isNotEmpty() && kataSandiBaru.isNotEmpty() && konfirmasiKataSandi.isNotEmpty() &&
                usernameBaru.isNotEmpty() && namaDepanBaru.isNotEmpty() && namaBelakangBaru.isNotEmpty() && noTeleponBaru.isNotEmpty() && kataSandiSaatIni.isNotEmpty()) {

                if (kataSandiBaru == konfirmasiKataSandi) {
                    reauthenticateUser(id, kataSandiSaatIni, emailBaru, kataSandiBaru, usernameBaru, namaDepanBaru, namaBelakangBaru, noTeleponBaru)
                } else {
                    binding.editConfirmPassword.error = "Kata Sandi Tidak Sama"
                    Log.e(TAG, "Kata sandi dan konfirmasi kata sandi tidak cocok")
                }
            } else {
                Toast.makeText(requireContext(), "Harap Mengisi Semua Data", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Semua bidang harus diisi")
            }
        } else {
            Toast.makeText(requireContext(), "Harap pilih gambar profil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserData(
        userId: String,
        emailBaru: String,
        kataSandiBaru: String,
        usernameBaru: String,
        namaDepanBaru: String,
        namaBelakangBaru: String,
        noTeleponBaru: String,
        selectedImgFile: File
    ) {
        Log.d(TAG, "Memperbarui data pengguna untuk userId: $userId")

        val storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId)
        storageRef.putFile(Uri.fromFile(selectedImgFile))
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        Log.d(TAG, "Berkas berhasil diunggah. URL Unduh: $downloadUrl")
                        val userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

                        val userData = HashMap<String, Any>()
                        userData["email"] = emailBaru
                        userData["username"] = usernameBaru
                        userData["firstName"] = namaDepanBaru
                        userData["lastName"] = namaBelakangBaru
                        userData["noTelepon"] = noTeleponBaru
                        userData["password"] = kataSandiBaru
                        userData["profilePicture"] = downloadUrl.toString()

                        userDatabaseRef.updateChildren(userData)
                            .addOnSuccessListener {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    sharedPref.updateUserDatas(
                                        userId,
                                        usernameBaru,
                                        namaDepanBaru,
                                        namaBelakangBaru,
                                        emailBaru,
                                        noTeleponBaru,
                                        kataSandiBaru,
                                        downloadUrl.toString()
                                    )
                                    withContext(Dispatchers.Main) {
                                        Glide.with(requireContext())
                                            .load(downloadUrl.toString())
                                            .into(binding.imgProfil)
                                    }
                                }
                                Toast.makeText(requireContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "Data pengguna berhasil diperbarui")
                                findNavController().navigateUp()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Kesalahan saat memperbarui data pengguna", e)
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




    private fun getProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            sharedPref.getAllUserData.collect { userData ->
                withContext(Dispatchers.Main) {
                    binding.etEmail.setText(userData.email)
                    binding.etUsername.setText(userData.username)
                    binding.etNamaDepan.setText(userData.firstName)
                    binding.etNamaBelakang.setText(userData.lastName)
                    binding.etPhone.setText(userData.phoneNumber)
                    binding.editPassword.setText(userData.password)
                    binding.editConfirmPassword.setText(userData.password)
                    val localImageFile = downloadImageAndSaveLocally(userData.profilePicture)

                    localImageFile?.let { file ->
                        selectedImg = Uri.fromFile(file)
                        Glide.with(requireContext())
                            .load(selectedImg)
                            .into(binding.imgProfil)
                    }
                }
            }
        }
    }

    private suspend fun downloadImageAndSaveLocally(imageUrl: String): File? = withContext(Dispatchers.IO) {
        val fileName = "profile_picture_${System.currentTimeMillis()}.jpg"
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

            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun reauthenticateUser(
        userId: String,
        kataSandiSaatIni: String,
        emailBaru: String,
        kataSandiBaru: String,
        usernameBaru: String,
        namaDepanBaru: String,
        namaBelakangBaru: String,
        noTeleponBaru: String
    ) {
        val user = auth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, kataSandiSaatIni)
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        Log.d(TAG, "Pengguna berhasil diautentikasi ulang.")
                        updateUserAuth(userId, emailBaru, kataSandiBaru, usernameBaru, namaDepanBaru, namaBelakangBaru, noTeleponBaru)
                    } else {
                        Log.e(TAG, "Autentikasi ulang gagal: ${reauthTask.exception?.message}")
                        Toast.makeText(requireContext(), "Autentikasi ulang gagal. Silakan periksa kata sandi saat ini Anda.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Log.e(TAG, "Tidak ada pengguna yang terautentikasi ditemukan")
        }
    }

    private fun updateUserAuth(
        userId: String,
        emailBaru: String,
        kataSandiBaru: String,
        usernameBaru: String,
        namaDepanBaru: String,
        namaBelakangBaru: String,
        noTeleponBaru: String
    ) {
        val user = auth.currentUser
        if (user != null) {
            user.updateEmail(emailBaru)
                .addOnCompleteListener { emailUpdateTask ->
                    if (emailUpdateTask.isSuccessful) {
                        Log.d(TAG, "Alamat email pengguna berhasil diperbarui.")
                        user.updatePassword(kataSandiBaru)
                            .addOnCompleteListener { passwordUpdateTask ->
                                if (passwordUpdateTask.isSuccessful) {
                                    Log.d(TAG, "Kata sandi pengguna berhasil diperbarui.")
                                    val imageFile = uriToFile(selectedImg!!, requireContext())
                                    val reducedImageFile = reduceFileImage(imageFile)
                                    val fixedImageFile = fixImageOrientation(reducedImageFile, requireContext())
                                    updateUserData(userId, emailBaru, kataSandiBaru, usernameBaru, namaDepanBaru, namaBelakangBaru, noTeleponBaru, fixedImageFile)
                                } else {
                                    Log.e(TAG, "Kesalahan saat memperbarui kata sandi: ${passwordUpdateTask.exception?.message}")
                                }
                            }
                    } else {
                        Log.e(TAG, "Kesalahan saat memperbarui email: ${emailUpdateTask.exception?.message}")
                    }
                }
        } else {
            Log.e(TAG, "Tidak ada pengguna yang terautentikasi ditemukan")
        }
    }

}
