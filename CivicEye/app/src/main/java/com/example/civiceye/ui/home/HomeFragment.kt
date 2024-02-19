package com.example.civiceye.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.civiceye.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val PERMISSION_REQUEST_CODE = 100
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val MAP_ACTIVITY_REQUEST_CODE = 107
    }

    private lateinit var imageView: ImageView
    private lateinit var cameraButton: Button

    private val takePictureResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                // Permission denied
            }
        }
    }
    private fun getLocation() {
        Log.d("HomeFragment", "Getting location...")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("HomeFragment", "Permission granted")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d("HomeFragment", "Location: ${location.latitude}, ${location.longitude}")
                        val locationTextView: TextView = binding.locationTextview
                        locationTextView.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                        Toast.makeText(requireContext(), "Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Log.d("HomeFragment", "Location is null")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HomeFragment", "Error getting location", e)
                }   
        } else {
            // Permission not granted, request it
            Log.d("HomeFragment", "Permission not granted")
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    
        if (requestCode == MAP_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val longitude = data?.getDoubleExtra("longitude", 0.0)
            val latitude = data?.getDoubleExtra("latitude", 0.0)
        
            // Handle the selected location
            val locationTextView: TextView = binding.locationTextview
            locationTextView.text = "Latitude: $latitude, Longitude: $longitude"
        }
    }

    private lateinit var seekBar: SeekBar
    private lateinit var seekBarValue: TextView

    val categoryToSubcategories = mapOf(
        "Category 1" to listOf("Subcategory 1.1", "Subcategory 1.2", "Subcategory 1.3"),
        "Category 2" to listOf("Subcategory 2.1", "Subcategory 2.2", "Subcategory 2.3"),
        "Category 3" to listOf("Subcategory 3.1", "Subcategory 3.2", "Subcategory 3.3")
    )
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val subcategoryAutocomplete: AutoCompleteTextView = binding.autoCompleteTextView
        val spinner1: Spinner = binding.spinner1
        val categories = categoryToSubcategories.keys.toList()
        spinner1.setSelection(0)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
        
                // Update subcategories when a category is selected
                val subcategories = categoryToSubcategories[selectedCategory] ?: listOf()
                val subadapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subcategories)
                subcategoryAutocomplete.setAdapter(subadapter)
                subcategoryAutocomplete.isEnabled = true
        
                // Clear inputs on change in category
                subcategoryAutocomplete.text.clear()
                seekBar.progress = 0
                imageView.setImageDrawable(null)
                subadapter.notifyDataSetChanged()
            }
        
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        val subcategories = arguments?.getStringArray("subcategories")?.toMutableList() ?: mutableListOf()
        val subadapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subcategories)
        subcategoryAutocomplete.setAdapter(subadapter)


        //image capture
        imageView = binding.imageView
        cameraButton = binding.cameraButton

        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureResult.launch(takePictureIntent)
            }
        }

        //Location 
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val locationButton: Button = binding.locationButton

        locationButton.setOnClickListener {
            // Show a dialog box asking the user to choose between GPS and map
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose Location Method")
            builder.setMessage("Do you want to use GPS or select a location on the map?")

            builder.setPositiveButton("GPS") { _, _ ->
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    getLocation()
                }
            }
            builder.setNegativeButton("Map") { _, _ ->
                val intent = Intent(requireContext(), MapActivity::class.java)
                startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE)
            }
            builder.show()
        }


        //slider
        seekBar = binding.seekBar
        seekBarValue = binding.seekBarValue
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBarValue.text = "$progress"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val submitButton: Button = binding.submitButton
        submitButton.setOnClickListener {
            val newSubcategory = subcategoryAutocomplete.text.toString()

            //image to base64
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

            /*
                can be converted back to normal image in java using-
                <
                    String base64Image = "...";
                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                >
            */
            //JSON Data
            val data = JSONObject().apply {
                put("category", spinner1.selectedItem)
                put("subcategory", newSubcategory)
                put("seekBarValue", seekBar.progress)
                put("image", base64Image) 
            }
            Log.d("HomeFragment", "Data: $data")
            Toast.makeText(requireContext(), "Data Converted to JSON", Toast.LENGTH_LONG).show()
        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}