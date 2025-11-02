package com.example.kursovaya.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kursovaya.R
import com.example.kursovaya.activity.LoginActivity
import com.example.kursovaya.adapter.MenuAdapter
import com.example.kursovaya.databinding.FragmentProfileBinding
import com.example.kursovaya.model.MenuItem
import com.example.kursovaya.repository.AuthRepository

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepository: AuthRepository
    private lateinit var menuAdapter: MenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        authRepository = AuthRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.appointmentHistoryButton).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_appointmentHistoryFragment)
        }

        setupMenu()
        loadUserData()
    }

    private fun setupMenu() {
        menuAdapter = MenuAdapter()
        binding.menuRecyclerView.adapter = menuAdapter

        val menuItems = listOf(
            MenuItem(
                R.drawable.ic_person,
                "Редактировать профиль"
            ) { showToast("Edit Profile clicked") },
            MenuItem(R.drawable.ic_calendar, "Мои записи") { 
                findNavController().navigate(R.id.action_profileFragment_to_appointmentHistoryFragment)
            },
            MenuItem(
                R.drawable.ic_calendar,
                "Уведомления",
                badgeCount = 3
            ) { showToast("Notifications clicked") },
            MenuItem(R.drawable.ic_home, "Настройки") { showToast("Settings clicked") },
            MenuItem(R.drawable.ic_arrow_back, "Выйти") {
                // Логика выхода из аккаунта
                authRepository.clearAuth()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        )
        menuAdapter.submitList(menuItems)
    }

    private fun loadUserData() {
        // Здесь должны быть данные реального пользователя
        binding.userNameTextView.text = "John Smith"
        binding.userEmailTextView.text = "john.smith@email.com"
        binding.appointmentsCountTextView.text = "12"
        // Установка изображения
        binding.userImageView.setImageResource(R.drawable.placeholder_doctor)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
