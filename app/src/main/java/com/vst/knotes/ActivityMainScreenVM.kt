package com.vst.knotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vst.knotes.RoomDataBase.NotePageRepository

class ActivityMainScreenVM(private val repository: NotePageRepository) : ViewModel() { // Add repository parameter
    private val _students = MutableLiveData<List<MainScreenDM>>()
    val students: LiveData<List<MainScreenDM>> get() = _students

    // Create an adapter instance
    val studentsAdapter: MainScreenRVAdapter by lazy {
        MainScreenRVAdapter(_students.value ?: emptyList())
    }

    init {
        // Initialize with some data
//        fetchStudents()
        fetchStudentsRDB()
    }

    private fun fetchStudentsRDB() {
        // Observe the LiveData from the repository
        repository.getAllNotes().observeForever { notes ->
            // Convert NotePageDO to MainScreenDM
            _students.value = notes.map { note -> MainScreenDM(note.id, note.title, note.description) }
            studentsAdapter.updateData(_students.value ?: emptyList())
        }
    }

    /*private fun fetchStudents() {
        // Assume this function fetches or initializes your student list
        _students.value = listOf(
            MainScreenDM("John Doe", "Present"),
            MainScreenDM("Jane Smith", "Absent"),
            // Add more students here...
        )
        // Notify adapter about data change
        studentsAdapter.updateData(_students.value ?: emptyList())
    }*/
}

