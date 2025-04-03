package com.vst.knotes.RoomDataBase

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NotePageRepository(context: Context) {
    private var roomService: QueryClass? = null
    private val databaseExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = DataBase.getInstance(context)
        roomService = db.roomService()
    }

    fun insert(note: NotePageDO) {
        databaseExecutor.execute { roomService?.insert(note) }
    }

    fun getLastInsertedId(): Long? {
        var lastId: Long? = null
        databaseExecutor.execute {
            lastId = roomService?.getLastInsertedId() // Ensure this method is defined in your DAO
        }
        return lastId
    }

    fun update(note: NotePageDO) {
        databaseExecutor.execute {
            roomService?.updateNotesData(note.id, note.title, note.description)
            // Handle potential errors here if necessary
        }
    }

    fun getAllNotes(): LiveData<List<NotePageDO>> {
        return roomService?.getAllNotes() ?: MutableLiveData(emptyList())
    }

    fun getNoteById(noteId: Long): NotePageDO? {
        return roomService?.getNoteById(noteId) // Directly return the result
    }

    // New method to count notes with the same title
    fun countNotesWithTitle(title: String): Int {
        var count = 0
        databaseExecutor.execute {
            count = roomService?.countNotesWithTitle(title) ?: 0
        }
        return count
    }
}
