package com.example.civiceye.ui.home
import WhistleDataAdapter
import java.text.SimpleDateFormat
import java.util.*

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
import android.util.Base64
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.civiceye.Constants
import com.example.civiceye.Constants.BASE_URL
import com.example.civiceye.R
import com.example.civiceye.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var CATEGORY = ""
    private var RADIUS = 1000.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Initialize spinners and set default values
        val radiusSpinner = root.findViewById<Spinner>(R.id.radius_spinner)
        val categorySpinner = root.findViewById<Spinner>(R.id.category_spinner)

        // Populate radiusSpinner with values from 50 to 20000
        val radiusValues = (500..5000 step 500).map { it.toString() }.toTypedArray()
        val radiusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, radiusValues)
        radiusSpinner.adapter = radiusAdapter

        // Set default value for radiusSpinner
        val defaultRadiusIndex = radiusValues.indexOf("1000")
        radiusSpinner.setSelection(defaultRadiusIndex)

        // Populate categorySpinner with predefined categories
        var categories = arrayOf("","Category 1", "Category 2", "Category 3")
        categories += getCategoriesFromSharedPref();

        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categorySpinner.adapter = categoryAdapter

        // Set default value for categorySpinner
        categorySpinner.setSelection(0) // "" is the first item


        // Set up spinner selection listeners
        var selectedRadius: Double = RADIUS;
        var selectedCategory: String = CATEGORY;

        radiusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedRadius = parent.getItemAtPosition(position).toString().toDouble()
                fetchWhistleData(selectedRadius, selectedCategory) { whistleDataList ->
                    // Update RecyclerView
                    val adapter = WhistleDataAdapter(whistleDataList) { whistleData ->
                        val whistleDescriptionFragment = WhistleDescriptionFragment()

                        // Pass whistle data to the fragment
                        val bundle = Bundle()
                        bundle.putSerializable("whistleData", whistleData)
                        whistleDescriptionFragment.arguments = bundle

                        // Show the fragment
                        whistleDescriptionFragment.show(childFragmentManager, "whistleDescription")
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(context)
                    binding.recyclerView.adapter = adapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedCategory = parent.getItemAtPosition(position).toString()
                fetchWhistleData(selectedRadius, selectedCategory) { whistleDataList ->
                    // Update RecyclerView
                    val adapter = WhistleDataAdapter(whistleDataList) { whistleData ->
                        val whistleDescriptionFragment = WhistleDescriptionFragment()

                        // Pass whistle data to the fragment
                        val bundle = Bundle()
                        bundle.putSerializable("whistleData", whistleData)
                        whistleDescriptionFragment.arguments = bundle

                        // Show the fragment
                        whistleDescriptionFragment.show(childFragmentManager, "whistleDescription")
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(context)
                    binding.recyclerView.adapter = adapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        return root
    }

    //request body

    private lateinit var requestBody: RequestBody
    private fun fetchWhistleData(radius: Double, category: String, callback: (List<WhistleData>) -> Unit) {
        val location = getLocation()
        if (location != null) {
            val (latitude, longitude) = location

            // Create request body with user's location, selected radius and category
            requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), JSONObject().apply {
                put("latitude", latitude)
                put("longitude", longitude)
                put("radius", radius)
                put("category", category)
            }.toString())

            // Rest of the code remains the same...
            // Make network request here and call callback with the result
            val request = Request.Builder()
                .url("${Constants.BASE_URL}/api/whistle/select")
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
                        Log.d("HomeFragment","Whistle List Transmission Successful :: $response");
                        response.body?.string()?.let { responseBody ->
                            val jsonArray = JSONArray(responseBody)
                            val whistleDataList = mutableListOf<WhistleData>()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val whistleData = WhistleData(
                                    jsonObject.getString("whistleId"),
                                    jsonObject.getString("category"),
                                    jsonObject.getString("subcategory"),
                                    jsonObject.getString("latitude"),
                                    jsonObject.getString("longitude"),
                                    jsonObject.getString("description"),
                                    jsonObject.getString("userRating"),
                                    jsonObject.getString("timestamp")
                                )
                                whistleDataList.add(whistleData)
                            }
                            // Switch back to the main thread
                            view?.post {
                                callback(whistleDataList)
                            }
                        }
                        Log.d("HomeFragment","Whistle List , parsed to jsonarray");
                    } else {
                        Log.d("HomeFragment","Whistle List Transmission Failed :: $response");
                    }
                }
            })
        }
        else {
            promptForLocation()
        }
    }

    private fun promptForLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the missing permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                0 // Request code
            )
        }else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    saveLocation(location.latitude, location.longitude)
                }
            }
        }
    }
    private fun saveLocation(latitude: Double, longitude: Double) {
        val sharedPreferences = requireActivity().getSharedPreferences("com.example.civiceye", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putFloat("latitude", latitude.toFloat())
            putFloat("longitude", longitude.toFloat())
            apply()
        }
    }

    private fun getLocation(): Pair<Double, Double>? {
        val sharedPreferences = requireActivity().getSharedPreferences("com.example.civiceye", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("latitude") && sharedPreferences.contains("longitude")) {
            val latitude = sharedPreferences.getFloat("latitude", 0f).toDouble()
            val longitude = sharedPreferences.getFloat("longitude", 0f).toDouble()
            return Pair(latitude, longitude)
        }
        return null
    }

    private fun getCategoriesFromSharedPref(): Array<String> {
        val sharedPref = requireContext().getSharedPreferences("com.example.civiceye", Context.MODE_PRIVATE)
        val sharedCTS = sharedPref.getString("categoryToSubcategories", null)
        val categories = mutableListOf<String>()
        if (sharedCTS != null) {
            val gson = Gson()
            val type = object : TypeToken<Map<String, List<String>>>() {}.type
            val categoryToSubcategories: Map<String, List<String>> = gson.fromJson(sharedCTS, type)
            categories.addAll(categoryToSubcategories.keys)
        }
        return categories.toTypedArray()
    }
}
