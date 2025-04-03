package com.vst.knotes.MVVM.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vst.knotes.MVVM.Services.ProjectRepository
import kotlinx.coroutines.launch


class LoginVM (private val repository: ProjectRepository): ViewModel(){
    init {
        // Initialize with some data
        fetchData()
    }
    private fun fetchData() {
        // Observe the LiveData from the repository
//        viewModelScope.launch {
//            repository.login(payload).collect { data ->
//                // Handle the data here
//                println(data)
//            }
//        }
    }
}