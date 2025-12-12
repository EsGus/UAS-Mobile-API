package com.example.uasmobileapi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etLocation: EditText
    private lateinit var etDescription: EditText
    private lateinit var etStatus: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add) // Pastikan mengarah ke layout yang baru dibuat

        // Inisialisasi View
        etTitle = findViewById(R.id.etTitle)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)
        etStatus = findViewById(R.id.etStatus)
        btnSave = findViewById(R.id.btnSave)
        progressBar = findViewById(R.id.progressBar)

        btnSave.setOnClickListener {
            saveEvent()
        }
    }

    private fun saveEvent() {
        val title = etTitle.text.toString()
        val date = etDate.text.toString()
        val time = etTime.text.toString()
        val location = etLocation.text.toString()
        val description = etDescription.text.toString()
        val status = etStatus.text.toString()

        // Validasi sederhana (opsional)
        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Judul dan Tanggal wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat model event baru (ID null karena auto-increment di database)
        val newEvent = EventModel(
            id = null,
            title = title,
            date = date,
            time = time,
            location = location,
            description = description,
            status = status
        )

        progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Panggil fungsi createEvent di API
                val response = RetrofitClient.instance.createEvent(newEvent)

                withContext(Dispatchers.Main) {
                    progressBar.isVisible = false
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddActivity, "Berhasil Menambah Event!", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke MainActivity
                    } else {
                        Toast.makeText(this@AddActivity, "Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.isVisible = false
                    Toast.makeText(this@AddActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}