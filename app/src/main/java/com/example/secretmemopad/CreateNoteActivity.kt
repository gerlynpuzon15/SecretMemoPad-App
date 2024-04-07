package com.example.secretmemopad

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.secretmemopad.databinding.ActivityCreateNoteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNoteBinding
    private val BASE_URL = "https://secretmemopad-us-to.preview-domain.com/public/api/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("USER_ID") ?: ""


        binding.button.setOnClickListener {
            val message = binding.Message.text.toString()
            val title = binding.Title.text.toString()

            if (title.isBlank() || message.isBlank()) {
                Toast.makeText(this, "Please enter title and message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val note = Noting(
                user_id = userId!!,
                note = message,
                title = title
            )
            submitNote(note)
        }
    }



    private fun submitNote(note: Noting) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(Api::class.java)
        val call = api.submitNote(note)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Note added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Note failed", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(applicationContext, "Error submitting Note: " + t.message, Toast.LENGTH_SHORT).show()

            }
        })
    }
}
