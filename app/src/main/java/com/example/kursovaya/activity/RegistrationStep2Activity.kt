package com.example.kursovaya.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kursovaya.R
import com.example.kursovaya.databinding.ActivityRegistrationStep2Binding
import java.util.Calendar

class RegistrationStep2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationStep2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.editTextBirthDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonCompleteRegistration.setOnClickListener {
            // TODO: Handle registration completion logic
            Toast.makeText(this, "Registration Complete!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.editTextBirthDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}
