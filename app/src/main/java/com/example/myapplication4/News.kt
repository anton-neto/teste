package com.example.myapplication4.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class News(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // O valor '0' será ignorado, e o Room gera automaticamente o id
    val title: String,
    val description: String,
    val url: String,
    val isMain: Boolean = false,
    val isInteresting: Boolean = false
)

