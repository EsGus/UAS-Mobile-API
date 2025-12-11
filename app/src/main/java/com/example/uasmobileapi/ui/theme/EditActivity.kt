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

class EditActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var etLocation: EditText
    private lateinit var etDescription: EditText
    private lateinit var etStatus: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar
    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Inisialisasi View
        etTitle = findViewById(R.id.etTitle)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)
        etStatus = findViewById(R.id.etStatus)
        btnSave = findViewById(R.id.btnSave)
        progressBar = findViewById(R.id.progressBar)

        // Ambil data yang dikirim dari MainActivity
        eventId = intent.getStringExtra("id")
        etTitle.setText(intent.getStringExtra("title"))
        etDate.setText(intent.getStringExtra("date"))
        etTime.setText(intent.getStringExtra("time"))
        etLocation.setText(intent.getStringExtra("location"))
        etDescription.setText(intent.getStringExtra("description"))
        etStatus.setText(intent.getStringExtra("status"))

        btnSave.setOnClickListener {
            updateEvent()
        }
    }

    private fun updateEvent() {
        val id = eventId ?: return
        val title = etTitle.text.toString()
        val date = etDate.text.toString()
        val time = etTime.text.toString()
        val location = etLocation.text.toString()
        val description = etDescription.text.toString()
        val status = etStatus.text.toString()

        val updatedEvent = EventModel(id, title, date, time, location, description, status)

        progressBar.isVisible = true
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val response = RetrofitClient.instance.updateEvent(id, updatedEvent)
                withContext(Dispatchers.Main) {
                    progressBar.isVisible = false
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditActivity, "Berhasil Update!", Toast.LENGTH_SHORT).show()
                        finish() // Tutup activity ini dan kembali ke Main
                    } else {
                        Toast.makeText(this@EditActivity, "Gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.isVisible = false
                    Toast.makeText(this@EditActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}