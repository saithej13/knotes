package com.vst.knotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vst.knotes.RoomDataBase.NotePageRepository
import java.lang.IllegalArgumentException

class ViewModelFactory(private val repository: NotePageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityMainScreenVM::class.java)) {
            return ActivityMainScreenVM(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
