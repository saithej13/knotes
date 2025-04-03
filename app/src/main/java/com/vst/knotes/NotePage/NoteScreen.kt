package com.vst.knotes.NotePage

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vst.knotes.MainScreenDM
import com.vst.knotes.RoomDataBase.NotePageDO
import com.vst.knotes.RoomDataBase.NotePageRepository
import com.vst.knotes.databinding.NoteScreenBinding

class NoteScreen : AppCompatActivity() {
    private lateinit var binding: NoteScreenBinding
    private lateinit var noteRepository: NotePageRepository
    private var id: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NoteScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository
        noteRepository = NotePageRepository(this)

        // Retrieve the intent
        val intent = intent
        val students: MainScreenDM? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("students", MainScreenDM::class.java)
        } else {
            intent.getParcelableExtra("students")
        }

        // If the student object is not null, populate the fields
        if (students != null) {
            id = students.id // Capture the ID for updates
            binding.title.setText(students.title)
            binding.description.setText(students.description)
        }

        // Place cursor at the end of the description field
        binding.description.setSelection(binding.description.text.length)
        binding.description.requestFocus()
    }

    override fun onBackPressed() {
        val title = binding.title.text.toString().trim()
        val description = binding.description.text.toString().trim()
        /*val qwe = noteRepository.getLastInsertedId() ?: 0L
        if (id == -1L) {
            val newNote = NotePageDO(id = qwe+1 , title = title, description = description)
            noteRepository.insert(newNote)
            Toast.makeText(this, "Note inserted", Toast.LENGTH_SHORT).show()
        }*/
        if (id == -1L) {
            if(title!=""&& description!=""){
                val newNote = NotePageDO(title = title, description = description)
                noteRepository.insert(newNote)
            }

            //Toast.makeText(this, "Note inserted", Toast.LENGTH_SHORT).show()
        } else {
            if(title!=""|| description!="") {
                val existingNote = NotePageDO(id = id, title = title, description = description)
                noteRepository.update(existingNote)
            }
            //Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
        }
        super.onBackPressed() // Call super to handle back press
    }
}
