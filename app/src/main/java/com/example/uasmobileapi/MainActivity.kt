package com.example.uasmobileapi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// Import FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var rvEvents: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAdd: FloatingActionButton // 1. Variabel Tombol Tambah
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Inisialisasi View
        rvEvents = findViewById(R.id.rvEvents)
        progressBar = findViewById(R.id.progressBar)
        fabAdd = findViewById(R.id.fabAdd) // Pastikan ID ini sama dengan di XML

        rvEvents.layoutManager = LinearLayoutManager(this)

        // 3. Logika Klik Tombol Tambah (Pindah ke AddActivity)
        fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(intent)
        }

        // Panggil fetchEvents pertama kali
        fetchEvents()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data saat kembali ke halaman ini (misal setelah add atau edit)
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
                            // Setup Adapter dengan aksi Edit dan Delete
                            eventAdapter = EventAdapter(events,
                                onEditClick = { event ->
                                    // Aksi Edit: Pindah ke EditActivity bawa data
                                    val intent = Intent(this@MainActivity, EditActivity::class.java)
                                    intent.putExtra("id", event.id)
                                    intent.putExtra("title", event.title)
                                    intent.putExtra("date", event.date)
                                    intent.putExtra("time", event.time)
                                    intent.putExtra("location", event.location)
                                    intent.putExtra("description", event.description)
                                    intent.putExtra("status", event.status)
                                    startActivity(intent)
                                },
                                onDeleteClick = { event ->
                                    // Aksi Delete: Konfirmasi dulu
                                    showDeleteConfirmation(event)
                                }
                            )
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
                }
            }
        }
    }

    private fun showDeleteConfirmation(event: EventModel) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Event")
            .setMessage("Yakin ingin menghapus ${event.title}?")
            .setPositiveButton("Ya") { _, _ ->
                deleteEvent(event.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteEvent(id: String?) {
        if (id == null) return

        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.deleteEvent(id)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Event Dihapus", Toast.LENGTH_SHORT).show()
                        fetchEvents() // Refresh list setelah hapus
                    } else {
                        Toast.makeText(this@MainActivity, "Gagal Hapus", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}