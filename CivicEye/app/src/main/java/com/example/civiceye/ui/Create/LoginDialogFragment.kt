package com.example.civiceye.ui.Create

import LoginRequest
import SignupRequest
import UserService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.civiceye.Constants
import com.example.civiceye.R
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginDialogFragment: DialogFragment()  {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userService = retrofit.create(UserService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullnameEditText: EditText = view.findViewById(R.id.fullname)
        val useridEditText: EditText = view.findViewById(R.id.userid)
        val loginButton: Button = view.findViewById(R.id.loginButton)
        val signupButton: Button = view.findViewById(R.id.signupButton)

        loginButton.setOnClickListener {
            val userId = useridEditText.text.toString()
            lifecycleScope.launch {
                val response = userService.login(userId)

                if (response.isSuccessful) {
                    // Login successful
                    Toast.makeText(requireContext(), "Login successful :: ${response.body()}", Toast.LENGTH_LONG).show()
                    dismiss()
                } else {
                    // Login failed
                    Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        signupButton.setOnClickListener {
            val fullName = fullnameEditText.text.toString()
            val userId = useridEditText.text.toString()
            lifecycleScope.launch {
                val response = userService.signup(SignupRequest(fullName, userId))
                Log.d("LoginDialogFragment", "Data: $response")
                if (response.isSuccessful) {
                    // Signup successful
                    Toast.makeText(requireContext(), "Signup successful :: ${response.body()}", Toast.LENGTH_LONG).show()
                    dismiss()
                } else {
                    // Signup failed
                    Toast.makeText(requireContext(), "Signup failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }
}