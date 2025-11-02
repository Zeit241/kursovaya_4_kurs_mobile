package com.example.kursovaya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kursovaya.R
import com.example.kursovaya.adapter.DateAdapter
import com.example.kursovaya.adapter.TimeAdapter
import com.example.kursovaya.databinding.FragmentBookingBinding
import com.example.kursovaya.model.DateItem
import com.example.kursovaya.model.Doctor
import com.example.kursovaya.model.TimeItem

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private lateinit var dateAdapter: DateAdapter
    private lateinit var timeAdapter: TimeAdapter

    private var selectedDate: DateItem? = null
    private var selectedTime: TimeItem? = null
    private var currentDoctor: Doctor? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupDateRecyclerView()
        setupTimeRecyclerView()

        val doctorId = arguments?.getString("DOCTOR_ID")
        loadDoctorInfo(doctorId)

        binding.confirmBookingButton.setOnClickListener {
            showConfirmationScreen()
        }

        binding.backToHomeButton.setOnClickListener {
            findNavController().popBackStack(R.id.nav_home, false)
        }

        binding.viewAppointmentsButton.setOnClickListener {
            findNavController().navigate(R.id.nav_queue)
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            if (binding.bookingConfirmationContainer.visibility == View.VISIBLE) {
                showSelectionScreen()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun loadDoctorInfo(doctorId: String?) {
        // In a real app, you would fetch this from a ViewModel or repository
        currentDoctor = getDummyDoctors().find { it.id == doctorId }
        currentDoctor?.let {
            binding.doctorNameTextView.text = it.name
            binding.doctorSpecialtyTextView.text = it.specialty
            binding.consultationFeeTextView.text = it.consultationFee
            // Load image using Glide or Picasso in a real app
        }
    }

    private fun setupDateRecyclerView() {
        val dates = getDummyDates()
        dateAdapter = DateAdapter(dates) { date ->
            selectedDate = date
            updateConfirmButtonState()
        }
        binding.dateRecyclerView.adapter = dateAdapter
        binding.dateRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupTimeRecyclerView() {
        val times = getDummyTimes()
        timeAdapter = TimeAdapter(times) { time ->
            selectedTime = time
            updateConfirmButtonState()
        }
        binding.timeRecyclerView.adapter = timeAdapter
        binding.timeRecyclerView.layoutManager = GridLayoutManager(context, 3)
    }

    private fun updateConfirmButtonState() {
        val isEnabled = selectedDate != null && selectedTime != null
        binding.confirmBookingButton.isEnabled = isEnabled
    }

    private fun showConfirmationScreen() {
        binding.bookingSelectionContainer.visibility = View.GONE
        binding.bookingConfirmationContainer.visibility = View.VISIBLE
        updateSummaryCard()
    }

    private fun showSelectionScreen() {
        binding.bookingConfirmationContainer.visibility = View.GONE
        binding.bookingSelectionContainer.visibility = View.VISIBLE
    }

    private fun updateSummaryCard() {
        val date = selectedDate?.dayOfMonth ?: ""
        val time = selectedTime?.time ?: ""
        val fee = currentDoctor?.consultationFee ?: ""

        binding.summaryDate.text = date
        binding.summaryTime.text = time
        binding.summaryFee.text = fee
    }

    private fun getDummyDates(): List<DateItem> {
        // ... (same as before)
        return listOf(
            DateItem("Пн", "1"),
            DateItem("Вт", "2"),
            DateItem("Ср", "3"),
            DateItem("Чт", "4"),
            DateItem("Пт", "5"),
            DateItem("Сб", "6"),
            DateItem("Вс", "7")
        )
    }

    private fun getDummyTimes(): List<TimeItem> {
        // ... (same as before)
        return listOf(
            TimeItem("09:00"), TimeItem("09:30"), TimeItem("10:00"),
            TimeItem("10:30"), TimeItem("11:00"), TimeItem("11:30"),
            TimeItem("14:00"), TimeItem("14:30"), TimeItem("15:00"),
            TimeItem("15:30"), TimeItem("16:00"), TimeItem("16:30")
        )
    }
    private fun getDummyDoctors(): List<Doctor> {
        return listOf(
            Doctor(
                "1",
                "Д-р. Анна Петрова",
                "Кардиолог",
                4.9,
                127,
                "15 лет",
                "Медцентр в центре города",
                "Доступен сегодня",
                "",
                "1500 ₽"
            ),
            Doctor(
                "2",
                "Д-р. Михаил Чен",
                "Невролог",
                4.9,
                127,
                "12 лет",
                "Городская больница",
                "Доступен завтра",
                "",
                "1800 ₽"
            ),
            Doctor(
                "3",
                "Д-р. Эмили Родригез",
                "Педиатр",
                4.8,
                98,
                "10 лет",
                "Детская клиника",
                "Доступен сегодня",
                "",
                "1200 ₽"
            ),
            Doctor(
                "4",
                "Д-р. Джеймс Уилсон",
                "Ортопед",
                4.7,
                84,
                "18 лет",
                "Центр спортивной медицины",
                "Доступен через 2 дня",
                "",
                "1600 ₽"
            ),
            Doctor(
                "5",
                "Д-р. Лиза Андерсон",
                "Дерматолог",
                4.8,
                112,
                "8 лет",
                "Клиника кожных заболеваний",
                "Доступен сегодня",
                "",
                "1400 ₽"
            ),
            Doctor(
                "6",
                "Д-р. Роберт Ким",
                "Терапевт",
                4.6,
                76,
                "20 лет",
                "Общественный центр здоровья",
                "Доступен завтра",
                "",
                "1000 ₽"
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
