package com.example.civiceye.ui.Create

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.civiceye.Constants
import com.example.civiceye.R
import com.example.civiceye.databinding.CreateWhistleBinding
import com.example.civiceye.databinding.FragmentHomeBinding
import com.example.civiceye.ui.home.HomeFragment
import com.example.civiceye.ui.home.MapActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.UUID

class CreateWhistle : Fragment() {
    private var _binding: CreateWhistleBinding? = null
    private val binding get() = _binding!!

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CreateWhistle.LOCATION_PERMISSION_REQUEST_CODE) {
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
                        latitude = location.latitude
                        longitude = location.longitude
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
                CreateWhistle.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CreateWhistle.MAP_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val longitude = data?.getDoubleExtra("longitude", 0.0)
            val latitude = data?.getDoubleExtra("latitude", 0.0)

            // Handle the selected location
            val locationTextView: TextView = binding.locationTextview
            locationTextView.text = "Latitude: $latitude, Longitude: $longitude"
            this.latitude = latitude ?: 0.0
            this.longitude = longitude ?: 0.0
        }
    }

    private lateinit var seekBar: SeekBar
    private lateinit var seekBarValue: TextView

    private lateinit var descriptionEditText: EditText

    val categoryToSubcategories = mapOf(
        "Category 1" to listOf("Subcategory 1.1", "Subcategory 1.2", "Subcategory 1.3"),
        "Category 2" to listOf("Subcategory 2.1", "Subcategory 2.2", "Subcategory 2.3"),
        "Category 3" to listOf("Subcategory 3.1", "Subcategory 3.2", "Subcategory 3.3")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var userId: String? = null;
        _binding = CreateWhistleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPref = requireContext().getSharedPreferences("CivicEye", Context.MODE_PRIVATE)
        if (!sharedPref.contains("userId")) {
            // User ID not found, generate a new one
            val userId = UUID.randomUUID().toString()
            with (sharedPref.edit()) {
                putString("userId", userId)
                apply()
            }
        } else {
            userId = sharedPref.getString("userId", "").toString()
        }

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
        imageView.setImageResource(R.drawable.no_photo)

        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CreateWhistle.PERMISSION_REQUEST_CODE
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
                        CreateWhistle.LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else {
                    getLocation()
                }
            }
            builder.setNegativeButton("Map") { _, _ ->
                val intent = Intent(requireContext(), MapActivity::class.java)
                startActivityForResult(intent, CreateWhistle.MAP_ACTIVITY_REQUEST_CODE)
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

        //description
        descriptionEditText = binding.description

        val submitButton: Button = binding.submitButton
        submitButton.setOnClickListener {


            val newSubcategory = subcategoryAutocomplete.text.toString()

            //image to base64
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()


            val description = descriptionEditText.text.toString().ifEmpty { null }

            //location and timestamp
            val location = JSONObject().apply {
                put("latitude", latitude)
                put("longitude", longitude)
            }
            val timestamp = System.currentTimeMillis()

            //JSON Data
            val jsonData = JSONObject().apply {
                put("userId",userId)
                put("category", spinner1.selectedItem)
                put("subcategory", newSubcategory)
                put("latitude", latitude)
                put("longitude", longitude)
                put("description", description)
                put("userRating",seekBarValue.text.toString().toInt())
                put("timestamp", timestamp)
            }

            Log.d("HomeFragment", "Data: $jsonData")
            Toast.makeText(requireContext(), "Data Converted to JSON", Toast.LENGTH_LONG).show()

            var strJsonData = jsonData.toString()

            // Multipart request body
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("json", strJsonData)
                .addFormDataPart("image", "image.png",
                    RequestBody.create("image/png".toMediaTypeOrNull(), byteArray))
                .build()

            // Create a POST request
//            val loggingInterceptor = HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            }
            val request = Request.Builder()
                .url("${Constants.BASE_URL}/api/whistle")
                .post(requestBody)
                .build()
            Log.d("HomeFragment","Request :: ${request.body}")

            // Send the request
            val client = OkHttpClient()
//            val client = OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .build()

            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("HomeFragment","IOException :: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("HomeFragment","Transmission Successful :: $response");
                    } else {
                        Log.d("HomeFragment","Transmission Failed :: $response");
                    }
                }
            })
        }
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}