package com.example.civiceye
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.civiceye.Constants.BASE_URL
import com.example.civiceye.databinding.ActivityMainBinding
import com.example.civiceye.ui.Create.LoginDialogFragment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home,R.id.create_whistle, R.id.navigation_dashboard, R.id.navigation_logout))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.create_whistle -> {
                    navController.navigate(R.id.create_whistle)
                    true
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                    true
                }
                R.id.navigation_logout -> {
                    // Perform logout operation
                    val sharedPref = getSharedPreferences("CivicEye", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        remove("userId")
                        apply()
                    }
                    // Show the LoginDialogFragment
                    LoginDialogFragment().show(supportFragmentManager, "loginDialog")
                    true
                }
                else -> false
            }
        }

        val sharedPref = getSharedPreferences("CivicEye", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", null)

        if (userId==null) {
            // If the user is not logged in, show the LoginDialogFragment
            LoginDialogFragment().show(supportFragmentManager, "loginDialog")
        }


    }

private fun getIssues(){
    val client=OkHttpClient();
    val urlString = "$BASE_URL/api/wstate/upvote/"
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

            val categoryToSubcategories = mapOf(
                "Category 1" to listOf("Subcategory 1.1", "Subcategory 1.2", "Subcategory 1.3"),
                "Category 2" to listOf("Subcategory 2.1", "Subcategory 2.2", "Subcategory 2.3"),
                "Category 3" to listOf("Subcategory 3.1", "Subcategory 3.2", "Subcategory 3.3")
            )

            val sharedPreferences = requireActivity().getSharedPreferences("com.example.civiceye", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("categoryToSubcategories", categoryToSubcategories)
                apply()
            }
        }
    })
}


}