package com.example.kursovaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kursovaya.model.AuthState
import com.example.kursovaya.repository.AuthRepository

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        _authState.value = authRepository.getAuthState()
    }

    fun login(token: String) {
        authRepository.saveAuthToken(token)
        _authState.value = AuthState.Authenticated(token)
    }

    fun logout() {
        authRepository.clearAuth()
        _authState.value = AuthState.Unauthenticated
    }
}