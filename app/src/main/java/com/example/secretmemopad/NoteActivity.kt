package com.example.secretmemopad

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.secretmemopad.databinding.ActivityNoteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var noteAdapter: NoteAdapter
    private val BASE_URL = "https://secretmemopad-us-to.preview-domain.com/public/api/"
    private val TAG = "NoteActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteAdapter = NoteAdapter()
        binding.noterecycle.adapter = noteAdapter
        binding.noterecycle.layoutManager = LinearLayoutManager(this)

        val userId = intent.getStringExtra("USER_ID")?.toIntOrNull() ?: -1

        Log.d(TAG, "User ID: $userId")

        if (userId != -1) {
            fetchOrders(userId)
        } else {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
        }

        binding.floatingActionButton1.setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java).apply {
                putExtra("USER_ID", getUserId(this@NoteActivity))

            }
            startActivity(intent)
        }

        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchOrders(userId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(Api::class.java)

        val call = apiService.getNotes(userId)
        call.enqueue(object : Callback<NoteResponse> {
            override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
                if (response.isSuccessful) {
                    val noteResponse = response.body()
                    val notes = noteResponse?.notes
                    noteAdapter.submitList(notes)
                } else {
                    // Handle error
                    Log.e(TAG, "Failed to fetch orders: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                // Handle failure
                Log.e(TAG, "Failed to fetch orders", t)
            }
        })
    }

    private fun getUserId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_ID", null)
    }

}
