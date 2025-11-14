package com.example.kursovaya.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kursovaya.databinding.ActivityRegistrationStep2Binding
import com.example.kursovaya.model.AuthState
import com.example.kursovaya.model.api.EmergencyContact
import com.example.kursovaya.repository.AuthApiRepository
import com.example.kursovaya.repository.AuthRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Calendar

class RegistrationStep2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationStep2Binding
    private val authApiRepository = AuthApiRepository()
    private lateinit var authRepository: AuthRepository
    
    private var email: String = ""
    private var phone: String = ""
    private var password: String = ""
    private var confirmPassword: String = ""
    private var fio: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        
        authRepository = AuthRepository(this)

        // Получаем данные из предыдущего шага
        email = intent.getStringExtra("email") ?: ""
        phone = intent.getStringExtra("phone") ?: ""
        password = intent.getStringExtra("password") ?: ""
        confirmPassword = intent.getStringExtra("confirmPassword") ?: ""
        fio = intent.getStringExtra("fio") ?: ""
        
        // Проверяем, что данные переданы
        if (email.isEmpty() || phone.isEmpty() || password.isEmpty() || fio.isEmpty()) {
            Toast.makeText(this, "Ошибка: данные не переданы", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.editTextBirthDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonCompleteRegistration.setOnClickListener { view ->
            // Валидация обязательных полей
            val birthDate = binding.editTextBirthDate.text.toString().trim()
            val selectedGenderId = binding.radioGroupGender.checkedRadioButtonId
            
            if (birthDate.isEmpty()) {
                Snackbar.make(view, "Пожалуйста, укажите дату рождения", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (selectedGenderId == -1) {
                Snackbar.make(view, "Пожалуйста, выберите пол", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Получаем пол (1 = мужской, 2 = женский)
            val gender: Short = if (selectedGenderId == binding.radioButtonMale.id) {
                1
            } else {
                2
            }
            
            // Форматируем дату в YYYY-MM-DD
            val formattedBirthDate = formatDateToApiFormat(birthDate)
            if (formattedBirthDate == null) {
                Snackbar.make(view, "Неверный формат даты", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Получаем опциональные поля
            val insuranceNumber = binding.editTextInsuranceNumber.text.toString().trim().takeIf { it.isNotEmpty() }
            val emergencyContactName = binding.editTextEmergencyContactName.text.toString().trim()
            val emergencyContactPhone = binding.editTextEmergencyContactPhone.text.toString().trim()
            val emergencyContactRelation = binding.editTextEmergencyContactRelation.text.toString().trim()
            
            // Создаем объект контактного лица, если заполнены все поля
            val emergencyContact: EmergencyContact? = if (
                emergencyContactName.isNotEmpty() && 
                emergencyContactPhone.isNotEmpty() && 
                emergencyContactRelation.isNotEmpty()
            ) {
                EmergencyContact(
                    name = emergencyContactName,
                    phone = emergencyContactPhone,
                    relation = emergencyContactRelation
                )
            } else {
                null
            }
            
            // Выполняем регистрацию через API
            binding.buttonCompleteRegistration.isEnabled = false
            binding.buttonCompleteRegistration.text = "Регистрация..."

            lifecycleScope.launch {
                authApiRepository.registerWithPatient(
                    email = email,
                    phone = phone,
                    password = password,
                    confirmPassword = confirmPassword,
                    fio = fio,
                    birthDate = formattedBirthDate,
                    gender = gender,
                    insuranceNumber = insuranceNumber,
                    emergencyContact = emergencyContact
                )
                    .onSuccess { response ->
                        // Сохраняем токен после успешной регистрации синхронно
                        authRepository.saveAuthToken(response.token)
                        
                        // Проверяем, что токен сохранился
                        val savedToken = authRepository.getAuthState()
                        if (savedToken is AuthState.Authenticated) {
                            Toast.makeText(
                                this@RegistrationStep2Activity,
                                "Регистрация успешна!",
                                Toast.LENGTH_SHORT
                            ).show()
                            
                            // Переходим в MainActivity, так как пользователь уже авторизован
                            val intent = Intent(this@RegistrationStep2Activity, com.example.kursovaya.activity.MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        } else {
                            // Если токен не сохранился, показываем ошибку
                            binding.buttonCompleteRegistration.isEnabled = true
                            binding.buttonCompleteRegistration.text = "Завершить регистрацию"
                            Snackbar.make(view, "Ошибка сохранения токена", Snackbar.LENGTH_LONG).show()
                        }
                    }
                    .onFailure { error ->
                        // Разблокируем кнопку
                        binding.buttonCompleteRegistration.isEnabled = true
                        binding.buttonCompleteRegistration.text = "Завершить регистрацию"
                        
                        val errorMessage = error.message ?: "Ошибка регистрации"
                        Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show()
                    }
            }
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
                // Форматируем дату для отображения
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.editTextBirthDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
    
    /**
     * Конвертирует дату из формата DD/MM/YYYY в YYYY-MM-DD для API
     */
    private fun formatDateToApiFormat(dateString: String): String? {
        return try {
            val parts = dateString.split("/")
            if (parts.size == 3) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val year = parts[2]
                "$year-$month-$day"
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
