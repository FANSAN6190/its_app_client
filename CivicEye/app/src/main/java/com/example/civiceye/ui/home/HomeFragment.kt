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
    private var LATITUDE = 24.4346089;
    private var LONGITUDE = 77.1619421;
    private var RADIUS = 1000.0;
    private var CATEGORY = "";

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Fetch whistle data from the server
        fetchWhistleData { whistleDataList ->
            // Set up RecyclerView
            val adapter = WhistleDataAdapter(whistleDataList)
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = adapter        }

        return root
    }

    //request body
    private val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), JSONObject().apply {
        put("latitude", LATITUDE)
        put("longitude", LONGITUDE)
        put("radius", RADIUS)
        put("category",CATEGORY)
    }.toString())

    private fun fetchWhistleData(callback: (List<WhistleData>) -> Unit) {
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
}
