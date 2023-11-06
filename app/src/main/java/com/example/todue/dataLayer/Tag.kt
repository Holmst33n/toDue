package com.example.todue.dataLayer

import androidx.room.Entity
import androidx.room.PrimaryKey

//Might be used in the future
@Entity
data class Tag(
    val title: String,
    val toDoAmount: Int,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
