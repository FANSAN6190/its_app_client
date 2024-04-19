package com.example.civiceye.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.civiceye.Constants.BASE_URL
import com.example.civiceye.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class WhistleDescriptionFragment : BottomSheetDialogFragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_whistle_description, container, false)

        // Retrieve whistle data from arguments
        val whistleData = arguments?.getSerializable("whistleData") as WhistleData

        // Display whistle data
        val whistleCategorySubcategory: TextView = view.findViewById(R.id.name_textview)
        val whistleTime: TextView = view.findViewById(R.id.timeStamp)
        val whistleUserRating: TextView = view.findViewById(R.id.userRating)
        val whistleDescription: TextView = view.findViewById(R.id.description_textview)
        val upvoteButton: Button = view.findViewById(R.id.whistleUpvoteButton)


        whistleCategorySubcategory.text = "${whistleData.category} - ${whistleData.subcategory}"

        val timestamp = whistleData.timestamp.toLong()
        val date = Date(timestamp)
        val format = SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault())
        whistleTime.text = format.format(date)

        whistleUserRating.text = "User Rating: ${whistleData.userRating}"
        whistleDescription.text = whistleData.description
        upvoteButton.setOnClickListener {
            if (canUpvote(whistleData.whistleId)) {
                sendUpvoteRequest(whistleData.whistleId)
                saveUpvoteTime(whistleData.whistleId)
                freezeButton(upvoteButton)
            } else {
                upvoteButton.isEnabled = false
            }
        }



        // Set the height of the LinearLayout to be half of the screen height
        val linearLayout: LinearLayout = view.findViewById(R.id.whistleDescriptionLinearLayout)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val layoutParams = linearLayout.layoutParams
        layoutParams.height = height / 2
        linearLayout.layoutParams = layoutParams

        return view
    }

    private fun sendUpvoteRequest(whistleId: String) {
        val client = OkHttpClient()

        val urlString = "$BASE_URL/api/wstate/upvote/$whistleId"
        val url = URL(urlString)

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                // handle the response
            }
        })
    }

    private fun canUpvote(whistleId: String): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("com.example.civiceye", Context.MODE_PRIVATE)
        val lastUpvoteTime = sharedPreferences.getLong(whistleId, 0L)
        val freezeTimeInSeconds = sharedPreferences.getLong("freezeTimeInSeconds", 60L) // default is 1 hour
        val freezeTimeInMillis = TimeUnit.SECONDS.toMillis(freezeTimeInSeconds)
        return System.currentTimeMillis() - lastUpvoteTime > freezeTimeInMillis
    }

    private fun saveUpvoteTime(whistleId: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("com.example.civiceye", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putLong(whistleId, System.currentTimeMillis())
            apply()
        }
    }

    private fun freezeButton(button: Button) {
        val sharedPreferences = requireActivity().getSharedPreferences("com.example.civiceye", Context.MODE_PRIVATE)
        val freezeTimeInSeconds = sharedPreferences.getLong("freezeTimeInSeconds", 3600L) // default is 1 hour
        val freezeTimeInMillis = TimeUnit.SECONDS.toMillis(10)

        button.isEnabled = false
        button.alpha = 0.5f // make the button dim

        Handler(Looper.getMainLooper()).postDelayed({
            button.isEnabled = true
            button.alpha = 1.0f // restore the button's opacity
        }, freezeTimeInMillis)
    }
}