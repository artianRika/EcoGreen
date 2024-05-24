package com.underoid.ecogreen.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.underoid.ecogreen.GlobalVars
import com.underoid.ecogreen.R

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val PHOTO_REQUEST_CODE = 2

    var lat: Double = 41.0
    var lng: Double = 20.0

    var latLngList = mutableListOf(
        LatLng( 41.9903221253715,20.9589318208824),
        LatLng(  41.9937660780103,20.9569714025797),
        LatLng( 41.9954732674999,20.960100150982),
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        val markSpotBtn: Button = view.findViewById(R.id.btn_markSpot)
        markSpotBtn.setOnClickListener {
            showMarkSpotDialog()



        }

        return view
    }

    private fun showMarkSpotDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom, null)
        val fullNameEditText = dialogView.findViewById<EditText>(R.id.editTextFullName)
        val surnameEditText = dialogView.findViewById<EditText>(R.id.editTextLocation)
        val uploadPhotoButton = dialogView.findViewById<Button>(R.id.buttonUploadPhoto)
        val submitButton = dialogView.findViewById<Button>(R.id.buttonSubmit)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Mark Spot")
            .setNegativeButton("Cancel", null)
            .create()

        uploadPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PHOTO_REQUEST_CODE)
        }


        submitButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString()
            val location = surnameEditText.text.toString()

            GlobalVars.setDiName(fullName)
            GlobalVars.setDiLocation(location)

            getCurrentLocation()


            latLngList.forEach{item ->
                placeMarker(item)
            }

            dialog.dismiss()
        }

        dialog.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data

            GlobalVars.setDiPhotoURI(selectedImageUri.toString())

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        latLngList.forEach{item ->
            placeMarker(item)
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)


                    lat = location.latitude
                    lng = location.longitude

                    //post in 'locations/:id'

                    Toast.makeText(
                        requireContext(),
                        "Current Location: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_LONG
                    ).show()


                    latLngList.add(currentLatLng)

                    latLngList.forEach{item ->
                        placeMarker(item)
                    }

                } else {
                    Toast.makeText(requireContext(), "Location should not be null", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to get location: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun placeMarker(latLng: LatLng) {

        gMap.addMarker(MarkerOptions().position(latLng).title("Current Location"))
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

    }
}
