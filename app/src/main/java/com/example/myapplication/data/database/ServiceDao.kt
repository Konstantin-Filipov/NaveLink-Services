package com.example.myapplication.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ServiceDao {

    @Insert
    suspend fun insertNote(service: Service)

    @Query("SELECT * FROM service_table WHERE id LIKE :serviceId LIMIT 1")
    fun findByID(serviceId: Int): Service

    @Update
    suspend fun updateService(service: Service)

    @Delete
    suspend fun deleteService(service: Service)
}