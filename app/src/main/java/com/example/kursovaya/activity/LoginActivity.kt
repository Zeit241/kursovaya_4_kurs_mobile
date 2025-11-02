package com.example.kursovaya.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kursovaya.R
import com.example.kursovaya.repository.AuthRepository
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var authRepository: AuthRepository
    val login = "login"
    val pass = "pass"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        authRepository = AuthRepository(this)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val registration = findViewById<TextView>(R.id.textViewSignUp)
        val rememberMe = findViewById<CheckBox>(R.id.checkboxRemember)
        val loginBtn = findViewById<Button>(R.id.buttonLogin)

        loginBtn.setOnClickListener { view ->
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // --- ИЗМЕНЕНИЕ 3: Улучшаем логику проверки ---
            // Сначала проверяем, не пустые ли поля
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(view, "Пожалуйста, заполните все поля", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener // Выходим из обработчика, если поля пустые
            }

            // Затем проверяем правильность логина и пароля
            if (email == login && password == pass) {
                val fakeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxx"
                authRepository.saveAuthToken(fakeToken)

                Toast.makeText(this, "Успешный вход!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Неверные данные!", Toast.LENGTH_SHORT).show()
            }

        }

        registration.setOnClickListener { view ->
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}