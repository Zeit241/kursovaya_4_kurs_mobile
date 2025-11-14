package com.example.kursovaya.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.kursovaya.R
import com.example.kursovaya.model.AuthState
import com.example.kursovaya.repository.AuthRepository
import com.example.kursovaya.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val authRepository by lazy { AuthRepository(this) }
    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(authRepository) as T
            }
        }
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Проверяем, авторизован ли пользователь
        if (authRepository.getAuthState() is AuthState.Unauthenticated) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            return
        }

        // Инициализируем RetrofitClient для добавления JWT токена
        com.example.kursovaya.api.RetrofitClient.init(this)

        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemReselectedListener { item ->
            val selectedTabId = item.itemId
            val currentGraph = navController.graph.findNode(selectedTabId)
            currentGraph?.let {
                navController.popBackStack(it.id, true)
                navController.navigate(it.id)
            }
        }
    }
}
