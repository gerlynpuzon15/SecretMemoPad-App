package com.example.secretmemopad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.secretmemopad.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val BASE_URL = "https://secretmemopad-us-to.preview-domain.com/public/api/"

    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val name = binding.Name.text.toString().trim()
            val email = binding.Email.text.toString().trim()
            val password = binding.Password.text.toString().trim()
            val confirmPassword = binding.ConfirmPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() &&
                password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                if (password == confirmPassword) {
                    registerUser(name, email, password)
                } else {
                    // Display an error message if passwords do not match
                    Log.e(TAG, "Passwords do not match")
                }
            } else {
                // Display an error message if any field is empty
                Log.e(TAG, "All fields are required")
            }
        }
    }
    private fun registerUser(name: String, email: String,password: String) {
        if (!email.contains("@gmail")) {
            // If the email does not contain "@gmail.com", show an error message
            Snackbar.make(binding.root, "Please enter a valid Gmail address", Snackbar.LENGTH_SHORT).show()
            Log.e(TAG, "Invalid email format")
            return
        }

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)

        val request = Register(name, email, password)

        api.register(request).enqueue(object : Callback<UserApi> {
            override fun onResponse(call: Call<UserApi>, response: Response<UserApi>) {
                if (response.isSuccessful) {
                    Snackbar.make(binding.root, "Registration successful", Snackbar.LENGTH_SHORT).show()
                    Log.d(TAG, "Registration successful")
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Snackbar.make(binding.root, "The Email is an Avaialbale: ${response.code()}", Snackbar.LENGTH_SHORT).show()
                    Log.e(TAG, "Registration failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserApi>, t: Throwable) {
                Snackbar.make(binding.root, "Registration failed1: ${t.message}", Snackbar.LENGTH_SHORT).show()
                Log.e(TAG, "Registration failed: ${t.message}", t)
            }
        })
    }




}