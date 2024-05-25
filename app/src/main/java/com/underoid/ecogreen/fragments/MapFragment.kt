package com.underoid.ecogreen.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.underoid.ecogreen.GlobalPostId
import com.underoid.ecogreen.GlobalVars
import com.underoid.ecogreen.R
import com.underoid.ecogreen.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var gMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val PHOTO_REQUEST_CODE = 2

    var lat: Double = 41.0
    var lng: Double = 20.0

    var latLngList = mutableListOf<LatLng>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        GlobalPostId.init(requireContext())

        val markSpotBtn: Button = view.findViewById(R.id.btn_markSpot)
        markSpotBtn.setOnClickListener {
            showMarkSpotDialog()

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchLocations()
    }

    private fun showMarkSpotDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom, null)
        val fullNameEditText = dialogView.findViewById<EditText>(R.id.editTextFullName)
        val surnameEditText = dialogView.findViewById<EditText>(R.id.editTextLocation)
        val uploadPhotoButton = dialogView.findViewById<Button>(R.id.buttonUploadPhoto)
        val submitButton = dialogView.findViewById<Button>(R.id.buttonSubmit)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Mark My Spot")
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





            getCurrentLocation(fullName, location)



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

    private fun showMarkerClickedDialog(fullName: String, locationName: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_marker_details, null)

        val fullNameTextView = dialogView.findViewById<TextView>(R.id.tv_FullName)
        val locationTextView = dialogView.findViewById<TextView>(R.id.tv_Location)
        val img = dialogView.findViewById<ImageView>(R.id.image)

        fullNameTextView.text = fullName
        locationTextView.text = locationName

        img.load("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/1200px-Cat03.jpg") {
            placeholder(R.drawable.loading_img)
            error(R.color.red)
        }

        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        gMap.setOnMarkerClickListener(this)

       // GlobalVars.setLatLngListSizee(latLngList.size)
        latLngList.forEach{item ->
            placeMarker(item)
        }
    }

    private fun updateMapMarkers() {
     //   gMap.clear() //CHECKKKK
        latLngList.forEach { item ->
            placeMarker(item)
        }
    }

    private fun getCurrentLocation(fullName: String, locationName: String) {
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

                    if (isLocationWithinRadius(currentLatLng, latLngList, 100)) {
                        Toast.makeText(requireContext(), "Location exists within 100 meters", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }


                    //post in 'locations/:id'

                    val location1 = com.underoid.ecogreen.model.Location(
                        id = GlobalPostId.getPostID(),
                        fullName = fullName,
                        locationName = locationName,
                        lat = lat,
                        lng = lng
                    )

                    GlobalPostId.incrementPostID()
                    val apiService = RetrofitInstance.instance


                    lifecycleScope.launch {
                        try {
                            apiService.sendLocation(location1.id, location1)
                            Toast.makeText(requireContext(), "Location added by ${fullName}", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Unable to add the location", Toast.LENGTH_LONG).show()
                        }
                    }

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


    private fun isLocationWithinRadius(currentLatLng: LatLng, locations: List<LatLng>, radius: Int): Boolean {
        val results = FloatArray(1)
        for (location in locations) {
            Location.distanceBetween(
                currentLatLng.latitude,
                currentLatLng.longitude,
                location.latitude,
                location.longitude,
                results
            )
            if (results[0] <= radius) {
                return true
            }
        }
        return false
    }

    private fun placeMarker(latLng: LatLng) {

        val markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_pin)
        val scaledMarkerBitmap = Bitmap.createScaledBitmap(markerBitmap, 80, 102, false)
        val markerIcon = BitmapDescriptorFactory.fromBitmap(scaledMarkerBitmap)

        gMap.addMarker(MarkerOptions().position(latLng).title("Current Location").icon(
            markerIcon
        ))
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val latLng = marker.position

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitInstance.instance.getLocations()
            val response = call.execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let { locations ->
                        val clickedLocation = locations.find { it.lat == latLng.latitude && it.lng == latLng.longitude }
                        if (clickedLocation != null) {
                            val name = clickedLocation.fullName
                            val location = clickedLocation.locationName
                            showMarkerClickedDialog(name, location)
                        } else {

                            Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch locations", Toast.LENGTH_LONG).show()
                }
            }
        }
        return true
    }

    private fun fetchLocations(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitInstance.instance.getLocations()
            val response = call.execute()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body()?.let { locations ->
                        latLngList = locations.map { LatLng(it.lat, it.lng) }.toMutableList()
                        updateMapMarkers()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch locations", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()

        GlobalVars.setLatLngListSizee(latLngList.size)
    }
}
