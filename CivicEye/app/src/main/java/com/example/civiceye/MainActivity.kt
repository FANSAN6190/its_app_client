package com.example.civiceye
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.civiceye.databinding.ActivityMainBinding
import com.example.civiceye.ui.Create.LoginDialogFragment


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
}