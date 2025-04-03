package com.vst.knotes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.View.Login
import com.vst.knotes.NotePage.NoteScreen
import com.vst.knotes.RoomDataBase.NotePageRepository
import com.vst.knotes.databinding.ActivityMainScreenBinding

class MainScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMainScreenBinding
    private lateinit var viewModel: ActivityMainScreenVM
    private lateinit var repository: NotePageRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = NotePageRepository(application) // Use application context for repository
        viewModel = ViewModelProvider(this, ViewModelFactory(repository))[ActivityMainScreenVM::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Initialize RecyclerView with adapter and layout manager
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = viewModel.studentsAdapter

        // Observe students and update the adapter accordingly
        viewModel.students.observe(this) { students ->
            if (students.isEmpty()) {
                binding.rvMain.visibility = View.GONE
                binding.tvnodatafound.visibility = View.VISIBLE
            } else {
                binding.rvMain.visibility = View.VISIBLE
                binding.tvnodatafound.visibility = View.GONE
                viewModel.studentsAdapter.updateData(students)
            }
        }

        binding.cvAdd.setOnClickListener {
            val intent = Intent(this, NoteScreen::class.java)
            startActivity(intent)
        }
    }
}
