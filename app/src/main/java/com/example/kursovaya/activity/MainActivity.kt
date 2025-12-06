package com.example.kursovaya.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavOptions
import com.example.kursovaya.R
import com.example.kursovaya.model.AuthState
import com.example.kursovaya.repository.AuthRepository
import com.example.kursovaya.repository.UserDataRepository
import com.example.kursovaya.repository.UserRepository
import com.example.kursovaya.viewmodel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

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
        
        // Инициализируем UserDataRepository
        UserDataRepository.init(this)

        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        
        // Флаг для предотвращения рекурсивной навигации при программном изменении selectedItemId
        var isNavigatingProgrammatically = false
        
        // При выборе элемента меню переключаемся на корневой фрагмент вкладки
        bottomNavigation.setOnItemSelectedListener { item ->
            if (isNavigatingProgrammatically) {
                return@setOnItemSelectedListener true
            }
            
            val selectedTabId = item.itemId
            
            // Навигация с очисткой back stack до стартового destination
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setLaunchSingleTop(true)
                .build()
            
            try {
                navController.navigate(selectedTabId, null, navOptions)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }
        
        // При повторном выборе той же вкладки возвращаемся к корневому фрагменту
        bottomNavigation.setOnItemReselectedListener { item ->
            val selectedTabId = item.itemId
            // Очищаем весь back stack до корневого фрагмента
            navController.popBackStack(selectedTabId, false)
        }
        
        // Синхронизируем выбранный элемент меню с текущим destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val rootId = getRootDestinationId(destination.id)
            if (bottomNavigation.selectedItemId != rootId && bottomNavigation.menu.findItem(rootId) != null) {
                isNavigatingProgrammatically = true
                bottomNavigation.selectedItemId = rootId
                isNavigatingProgrammatically = false
            }
        }
        
        // Загружаем данные пользователя при запуске приложения
        loadUserData()
    }
    
    /**
     * Определяет корневой ID вкладки для заданного destination ID
     */
    private fun getRootDestinationId(destinationId: Int): Int {
        // Основные вкладки нижнего меню
        val mainTabs = setOf(
            R.id.nav_home,
            R.id.nav_doctors,
            R.id.nav_queue,
            R.id.nav_map,
            R.id.nav_profile
        )
        
        if (destinationId in mainTabs) {
            return destinationId
        }
        
        // Явные маппинги для фрагментов профиля (проверяем ПЕРЕД циклом по NavGraph)
        if (destinationId == R.id.appointmentHistoryFragment || 
            destinationId == R.id.appointmentDetailsFragment ||
            destinationId == R.id.editProfileFragment) {
            return R.id.nav_profile
        }
        
        // Для фрагментов докторов
        if (destinationId == R.id.doctorProfileFragment || 
            destinationId == R.id.bookingFragment ||
            destinationId == R.id.doctorsFragment) {
            return R.id.nav_doctors
        }
        
        // Проверяем, принадлежит ли destination к одной из вложенных навигаций
        val graph = navController.graph
        for (tabId in mainTabs) {
            val tabNode = graph.findNode(tabId)
            if (tabNode != null) {
                // Если это navigation node, проверяем его дочерние элементы
                if (tabNode is androidx.navigation.NavGraph) {
                    if (tabNode.findNode(destinationId) != null) {
                        return tabId
                    }
                }
                // Если это обычный fragment и он совпадает
                if (tabNode.id == destinationId) {
                    return tabId
                }
            }
        }
        
        // По умолчанию возвращаем текущий destination
        return destinationId
    }
    
    private fun loadUserData() {
        lifecycleScope.launch {
            // Сначала проверяем, есть ли сохраненные данные
            val cachedUser = UserDataRepository.getCurrentUser()
            if (cachedUser != null) {
                Log.d("MainActivity", "Используются кэшированные данные пользователя: ${cachedUser.email}")
            } else {
                // Если нет кэша, загружаем с сервера
                Log.d("MainActivity", "Загрузка данных пользователя с сервера...")
                val userRepository = UserRepository(this@MainActivity)
                userRepository.getCurrentUser()
                    .onSuccess { user ->
                        Log.d("MainActivity", "Данные пользователя успешно загружены: ${user.email}")
                    }
                    .onFailure { error ->
                        Log.e("MainActivity", "Ошибка загрузки данных пользователя", error)
                    }
            }
        }
    }
}
