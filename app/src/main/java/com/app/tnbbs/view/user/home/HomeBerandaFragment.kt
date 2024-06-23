package com.app.tnbbs.view.user.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.app.tnbbs.R
import com.app.tnbbs.databinding.FragmentHomeBerandaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeBerandaFragment : Fragment() {
    lateinit var binding : FragmentHomeBerandaBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isMediaPermissionGranted = false
    private var isStoragePermissionGranted = false
    private var isLocationPermissionGranted = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBerandaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted
            if (isLocationPermissionGranted) {
                getCurrentLocation()
            }
            isStoragePermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isStoragePermissionGranted
            isMediaPermissionGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: isMediaPermissionGranted
        }

        requestPermission()
        displayCurrentDate()

        binding.btnInformasi.setOnClickListener{
            findNavController().navigate(R.id.action_homeBerandaFragment_to_informasiWisataFragment)
        }
        binding.btnEtiket.setOnClickListener{
            findNavController().navigate(R.id.action_homeBerandaFragment_to_homePesanTiketFragment)
        }
    }

    private fun displayCurrentDate() {
        val date = Date()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val formattedDate = dateFormat.format(date)
        binding.tvTanggal.text = formattedDate
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationTask: Task<Location> = fusedLocationClient.lastLocation
            locationTask.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    getAddressFromLocation(latitude, longitude)
                } else {
                    binding.tvLokasi.text = "Location not available"
                }
            }.addOnFailureListener {
                binding.tvLokasi.text = "Failed to get location"
            }
        } else {
            binding.tvLokasi.text = "Location permission not granted"
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses!!.isNotEmpty()) {
            val address = addresses[0]
            val province = address.adminArea ?: "Unknown Province"
//            val regency = address.subAdminArea ?: "Unknown Regency"
//            val district = address.locality ?: "Unknown District"
//            val village = address.subLocality ?: "Unknown Village"

            var district = address.locality ?: "Kecamatan tidak diketahui"
            if (district.startsWith("Kecamatan ", true)) {
                district = district.substringAfter("Kecamatan ", "")
            }

            var regency = address.subAdminArea ?: "Kabupaten tidak diketahui"
            if (regency.startsWith("Kabupaten ", true)) {
                regency = regency.substringAfter("Kabupaten ", "")
            }

            binding.tvLokasi.text = "$province, $regency, $district"
        } else {
            binding.tvLokasi.text = "Address not available"
        }
    }

    private fun requestPermission() {
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isStoragePermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isMediaPermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            isMediaPermissionGranted = isStoragePermissionGranted
        }

        val permissionsToRequest = mutableListOf<String>()

        if (!isLocationPermissionGranted) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            getCurrentLocation()
        }

        if (!isStoragePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isMediaPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}