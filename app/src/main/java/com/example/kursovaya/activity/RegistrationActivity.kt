package com.example.kursovaya.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kursovaya.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.checkboxTerms.setOnCheckedChangeListener { _, isChecked ->
            binding.buttonRegister.isEnabled = isChecked
        }

        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.textViewLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.buttonRegister.setOnClickListener {
            val fullName = binding.editTextFullName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val phone = binding.editTextPhone.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Пароли не одинаковые", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Передаем данные на следующий шаг регистрации
            val intent = Intent(this, RegistrationStep2Activity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("phone", phone)
            intent.putExtra("password", password)
            intent.putExtra("confirmPassword", confirmPassword)
            intent.putExtra("fio", fullName)
            startActivity(intent)
        }
    }
}
