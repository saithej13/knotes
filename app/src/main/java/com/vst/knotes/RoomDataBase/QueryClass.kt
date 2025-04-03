package com.vst.knotes.RoomDataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QueryClass {
    @Insert
    fun insert(note: NotePageDO)

    @Query("SELECT last_insert_rowid()") // This gets the last inserted row ID
    fun getLastInsertedId(): Long

    @Query("SELECT * FROM tblNotes")
    fun getAllNotes(): LiveData<List<NotePageDO>>

    @Query("SELECT * FROM tblNotes WHERE id = :id")
    fun getNoteById(id: Long): NotePageDO? // Return type is NotePageDO?

    @Query("UPDATE tblNotes SET title = :title, description = :description WHERE id = :id")
    fun updateNotesData(id: Long, title: String, description: String)

    @Query("DELETE FROM tblNotes WHERE id = :id")
    fun deleteById(id: Long)

    // New method to count notes with the same title
    @Query("SELECT COUNT(*) FROM tblNotes WHERE title = :title")
    fun countNotesWithTitle(title: String): Int
}
