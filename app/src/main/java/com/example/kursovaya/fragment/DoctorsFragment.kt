package com.example.kursovaya.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kursovaya.R
import com.example.kursovaya.adapter.DoctorsAdapter
import com.example.kursovaya.databinding.FragmentDoctorsBinding
import com.example.kursovaya.model.Doctor
import com.example.kursovaya.model.api.DoctorApi
import com.example.kursovaya.model.api.Specialization
import com.example.kursovaya.model.api.toImageDataUri
import com.example.kursovaya.repository.DoctorsRepository
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DoctorsFragment : Fragment() {

    private var _binding: FragmentDoctorsBinding? = null
    private val binding get() = _binding!!
    private lateinit var doctorsRepository: DoctorsRepository
    private lateinit var doctorsAdapter: DoctorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorsBinding.inflate(inflater, container, false)
        // Убеждаемся, что RetrofitClient инициализирован
        com.example.kursovaya.api.RetrofitClient.init(requireContext())
        doctorsRepository = DoctorsRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        loadSpecializations()
        loadDoctors()
    }

    private fun setupRecyclerView() {
        doctorsAdapter = DoctorsAdapter(
            doctors = emptyList(),
            onDoctorClicked = { doctor ->
                val bundle = bundleOf("DOCTOR_ID" to doctor.id)
                findNavController().navigate(
                    R.id.action_doctorsFragment_to_doctorProfileFragment,
                    bundle
                )
            },
            onBookClicked = { doctor ->
                val bundle = bundleOf("DOCTOR_ID" to doctor.id)
                findNavController().navigate(
                    R.id.action_doctorsFragment_to_bookingFragment,
                    bundle
                )
            }
        )

        binding.recyclerViewDoctors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = doctorsAdapter
        }
    }

    private var searchJob: Job? = null
    private var isProgrammaticTextChange = false // Флаг для программного изменения текста

    private fun setupSearch() {
        // Кнопка поиска
        binding.searchButton.setOnClickListener {
            performSearch()
        }

        binding.searchInputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Поиск при изменении текста (с задержкой)
        binding.searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Пропускаем автоматический поиск при программном изменении текста
                if (isProgrammaticTextChange) {
                    return
                }

                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(500) // Задержка 500мс перед поиском
                    if (_binding != null) {
                        performSearch()
                    }
                }
            }
        })
    }

    private fun loadSpecializations() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                doctorsRepository.getSpecializations()
                    .onSuccess { specializations ->
                        if (_binding == null) return@onSuccess
                        setupFilters(specializations)
                        Log.d("DoctorsFragment", "Loaded ${specializations.size} specializations")
                    }
                    .onFailure { error ->
                        if (_binding == null) return@launch
                        Log.e("DoctorsFragment", "Error loading specializations", error)
                        // Не показываем ошибку пользователю, просто не загружаем фильтры
                    }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                if (_binding == null) return@launch
                Log.e("DoctorsFragment", "Unexpected error loading specializations", e)
            }
        }
    }
    
    private fun setupFilters(specializations: List<Specialization>) {
        binding.specialtyChipGroup.removeAllViews()

        for (specialization in specializations) {
            val chip = Chip(requireContext()).apply {
                text = specialization.name
                isClickable = true
                isCheckable = true
                id = View.generateViewId() // Генерируем уникальный ID для чипа
            }

            binding.specialtyChipGroup.addView(chip)
        }

        // Обработчик изменения выбора в ChipGroup
        binding.specialtyChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            // Отменяем текущий поиск
            searchJob?.cancel()

            if (checkedIds.isEmpty()) {
                // Если ничего не выбрано, показываем всех врачей
                isProgrammaticTextChange = true
                binding.searchInputEditText.setText("")
                binding.searchInputEditText.post {
                    isProgrammaticTextChange = false
                }
                loadDoctors(query = null)
            } else {
                // Находим выбранный чип и выполняем поиск по его специальности
                val checkedChipId = checkedIds[0]
                val checkedChip = group.findViewById<Chip>(checkedChipId)
                checkedChip?.let {
                    val specialtyName = it.text.toString()
                    isProgrammaticTextChange = true
                    binding.searchInputEditText.setText("")
                    binding.searchInputEditText.post {
                        isProgrammaticTextChange = false
                    }
                    searchBySpecialty(specialtyName)
                }
            }
        }
    }

    private fun searchBySpecialty(specialty: String) {
        loadDoctors(query = specialty)
    }

    private fun performSearch() {
        val query = binding.searchInputEditText.text.toString().trim()
        // Снимаем выделение с чипов при поиске по тексту
        if (query.isNotEmpty()) {
            clearChipSelection()
        }
        loadDoctors(query = if (query.isNotEmpty()) query else null)
    }

    private fun clearChipSelection() {
        val checkedChipId = binding.specialtyChipGroup.checkedChipId
        if (checkedChipId != View.NO_ID) {
            binding.specialtyChipGroup.clearCheck()
        }
    }

    private fun loadDoctors(query: String? = null) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                doctorsRepository.getDoctors(query = query)
                    .onSuccess { doctorsApi ->
                        // Проверяем, что view еще существует
                        if (_binding == null) return@onSuccess

                        val doctors = doctorsApi.map { it.toDoctor() }
                        doctorsAdapter.updateDoctors(doctors)
                        updateResultsCount(doctors.size)
                        updateEmptyState(doctors.isEmpty())
                        Log.d("DoctorsFragment", "Loaded ${doctors.size} doctors")
                    }
                    .onFailure { error ->
                        // Игнорируем отмену корутины
                        if (error is kotlinx.coroutines.CancellationException) {
                            return@onFailure
                        }

                        // Проверяем, что view еще существует
                        if (_binding == null) return@onFailure

                        Log.e("DoctorsFragment", "Error loading doctors", error)
                        Snackbar.make(
                            binding.root,
                            "Ошибка загрузки врачей: ${error.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Игнорируем отмену корутины
                throw e
            } catch (e: Exception) {
                if (_binding == null) return@launch
                Log.e("DoctorsFragment", "Unexpected error", e)
            }
        }
    }

    private fun updateResultsCount(count: Int) {
        if (_binding == null) return
        binding.resultsCountTextView.text = when (count) {
            0 -> ""
            1 -> "Найден 1 врач"
            else -> "Найдено $count врачей"
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (_binding == null) return
        if (isEmpty) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewDoctors.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewDoctors.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Отменяем все активные поиски
        searchJob?.cancel()
        searchJob = null
        _binding = null
    }
}

// Функция расширения для конвертации DoctorApi в Doctor
private fun DoctorApi.toDoctor(): Doctor {
    val fullName = buildString {
        append(user.lastName)
        if (user.firstName.isNotEmpty()) {
            append(" ${user.firstName}")
        }
        if (user.middleName?.isNotEmpty() == true) {
            append(" ${user.middleName}")
        }
    }.trim()

    // Формируем строку специализаций из массива
    val specialtyText = if (!specializations.isNullOrEmpty()) {
        specializations.joinToString(", ") { it.name }
    } else {
        bio ?: "Врач"
    }

    return Doctor(
        id = id.toString(),
        name = fullName.ifEmpty { user.email },
        specialty = specialtyText,
        rating = rating ?: 0.0,
        reviews = reviewCount ?: 0,
        experience = "$experienceYears лет",
        location = "",
        availability = "",
        image = photoUrl.toImageDataUri(),
        consultationFee = ""
    )
}
