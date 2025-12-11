package com.example.uasmobileapi

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var rvEvents: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Ini menghubungkan ke Layout XML

        rvEvents = findViewById(R.id.rvEvents)
        progressBar = findViewById(R.id.progressBar)

        rvEvents.layoutManager = LinearLayoutManager(this)

        fetchEvents()
    }

    private fun fetchEvents() {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getAllEvents()
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val events = response.body()?.data
                        if (events != null) {
                            eventAdapter = EventAdapter(events)
                            rvEvents.adapter = eventAdapter
                        } else {
                            Toast.makeText(this@MainActivity, "Data Kosong", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Gagal Koneksi: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", e.toString())
                }
            }
        }
    }
}