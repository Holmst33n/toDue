package com.example.todue.dataLayer.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    /*
    @Query("SELECT * FROM tag")
    fun observeAll(): Flow<List<Tag>>

     */

    @Insert
    suspend fun createTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("SELECT * FROM tag GROUP BY title")
    fun getTagsOrderedByTitle(): Flow<List<Tag>>

    @Query("UPDATE tag SET sort = 1 WHERE id = :tagId")
    suspend fun sortByThisTag(tagId: Int)

    @Query("UPDATE tag SET sort = 0 WHERE id = :tagId")
    suspend fun dontSortByThisTag(tagId: Int)

    @Query("SELECT (EXISTS (SELECT * FROM tag WHERE sort = 1))")
    suspend fun checkIfSortByTags(): Boolean

    @Query("UPDATE tag SET sort = 0")
    suspend fun resetTagSort()

    @Query("SELECT (EXISTS (SELECT * FROM tag WHERE title = :tagTitle))")
    suspend fun checkIfTagExists(tagTitle: String): Boolean

    @Query("UPDATE tag SET toDoAmount = toDoAmount + 1 WHERE title = :tagTitle")
    suspend fun increaseToDoAmount(tagTitle: String)

    @Query("UPDATE tag SET toDoAmount = toDoAmount -1 WHERE title = :tagTitle")
    suspend fun decreaseToDoAmount(tagTitle: String)

    @Query("DELETE FROM tag WHERE toDoAmount IS 0")
    suspend fun deleteUnusedTags()
}