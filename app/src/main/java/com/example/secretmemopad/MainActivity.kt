package com.example.secretmemopad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.secretmemopad.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val BASE_URL = "https://secretmemopad-us-to.preview-domain.com/public/api/"
    private val authService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Api::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val email = binding.Email.text.toString().trim()
            val password = binding.Password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Snackbar.make(binding.root, "Please enter email and password", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.Register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    private fun login(email: String, password: String) {
        val request = Login(email, password)
        authService.loginapi(request).enqueue(object : Callback<UserApi> {
            override fun onResponse(call: Call<UserApi>, response: Response<UserApi>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        val authToken = apiResponse.token
                        val userId = apiResponse.user?.id
                        if (!authToken.isNullOrEmpty() && userId != null) {
                            Log.d("SignInActivity", "Authentication token received: $authToken")
                            saveUserId(userId.toString()) // Save user ID as String
                            val intent = Intent(this@MainActivity, NoteActivity::class.java)
                            intent.putExtra("USER_ID", userId.toString()) // Pass user ID as String
                            intent.putExtra("AUTH_TOKEN", authToken)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("SignInActivity", "Authentication token or user ID is empty or null")
                            Snackbar.make(binding.root, "Authentication token or user ID not found", Snackbar.LENGTH_SHORT).show()
                        }
                    } else {
                        Snackbar.make(binding.root, "Response body is null", Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        404 -> "User not found. Please check your email and password."
                        else -> "Sign-in failed: ${response.code()}"
                    }
                    Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserApi>, t: Throwable) {
                Log.e("SignInActivity", "Sign-in failed: ${t.message}")
                Snackbar.make(binding.root, "Sign-in failed. Please check your internet connection", Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserId(userId: String?) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_ID", userId)
        editor.apply()
    }


}
