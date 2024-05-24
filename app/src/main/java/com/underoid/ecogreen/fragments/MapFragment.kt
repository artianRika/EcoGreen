package com.underoid.ecogreen.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
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


            ////needs sth..
            val currentLocation = LatLng(42.006191, 20.959682)
            gMap.addMarker(MarkerOptions().position(currentLocation).title("Marked Spot"))
            gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
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

            Toast.makeText(requireContext(), "${GlobalVars.getDiURI()}", Toast.LENGTH_LONG).show()

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
        // initial settup for th....
    }
}
