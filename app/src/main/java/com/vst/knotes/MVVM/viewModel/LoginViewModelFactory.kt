package com.vst.knotes.MVVM.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vst.knotes.MVVM.Services.ProjectRepository
import java.lang.IllegalArgumentException

class LoginViewModelFactory(private val repository: ProjectRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginVM::class.java)) {
            return LoginVM(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}