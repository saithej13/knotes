package com.vst.knotes

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
//import androidx.media3.common.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.vst.knotes.NotePage.NoteScreen
import com.vst.knotes.databinding.MainScreenCellBinding

//import kotlinx.coroutines.flow.internal.NoOpContinuation.context
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class MainScreenRVAdapter(private var students: List<MainScreenDM>) :
    RecyclerView.Adapter<MainScreenRVAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MainScreenCellBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(student: MainScreenDM) {
            Log.d("MainScreenRVAdapter", "Binding student: ${student.title}")
            val titleLimit = 150 // Example limit
            if (!student.title.isNullOrBlank()) {
                binding.title.text = if (student.title.length > 80) {
                    student.title.substring(0, 80) + "..." // Add ellipsis if too long
                } else {
                    student.title
                }
            } else {
                binding.title.text = "" // Clear the title text if it's blank
                binding.title.hint = "Title" // Set the hint to "Title"
            }

            if (!student.description.isNullOrBlank()) {
                binding.description.text = if (student.description.length > 80) {
                    student.description.substring(0, 80) + "..." // Add ellipsis if too long
                } else {
                    student.description
                }
            } else {
                binding.description.text = "" // Clear the title text if it's blank
                binding.description.hint = "Description" // Set the hint to "Title"
            }

           /* if (!student.description.isNullOrBlank()) {
                binding.description.text =
                    if (student.description.length > 320) {
                        student.description.substring(0, 320) + "..." // Add ellipsis if too long
                    } else student.description

            }*/


//            binding.title.text = student.title
//            binding.description.text = student.description
            binding.executePendingBindings()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newStudents: List<MainScreenDM>) {
        students = newStudents
        notifyDataSetChanged() // Update the adapter with new data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MainScreenCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(students[position])
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NoteScreen::class.java)
            intent.putExtra("students", students[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = students.size
}