package com.example.myapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_table")
data class Service(

    @PrimaryKey
    var id: Int,

    var note: String
)