package com.example.kursovaya.fragment

import QueueAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kursovaya.databinding.FragmentQueueBinding
import com.example.kursovaya.model.QueueItem

class QueueFragment : Fragment() {

    private var _binding: FragmentQueueBinding? = null
    private val binding get() = _binding!!
    private lateinit var queueAdapter: QueueAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQueueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadQueueData()
    }

    private fun setupRecyclerView() {
        queueAdapter = QueueAdapter()
        binding.queueRecyclerView.apply {
            adapter = queueAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadQueueData() {
        // Здесь вы будете получать данные из ViewModel или репозитория
        val mockQueueData = listOf(
            QueueItem(
                id = "1",
                doctorName = "Dr. Sarah Johnson",
                specialty = "Cardiologist",
                currentNumber = 12,
                yourNumber = 15,
                estimatedWait = "15 mins",
                status = "waiting",
                peopleAhead = 3,
                image = "placeholder_doctor" // Используйте имя ресурса drawable
            ),
            QueueItem(
                id = "2",
                doctorName = "Dr. Michael Chen",
                specialty = "Neurologist",
                currentNumber = 8,
                yourNumber = 9,
                estimatedWait = "5 mins",
                status = "next",
                peopleAhead = 1,
                image = "placeholder_doctor_2"
            )
        )

        // Показываем/скрываем заглушку и список
        binding.queueRecyclerView.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
        queueAdapter.submitList(mockQueueData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
