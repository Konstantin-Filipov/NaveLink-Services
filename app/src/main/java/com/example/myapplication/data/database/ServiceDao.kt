package com.example.myapplication.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service)

    @Query("SELECT * FROM service_table WHERE id LIKE :serviceId LIMIT 1")
    suspend fun findById(serviceId: Int): Service

    @Query("SELECT note FROM service_table WHERE id LIKE :serviceId LIMIT 1")
    suspend fun getNoteByID(serviceId: Int): String

    @Query("SELECT * FROM service_table")
    fun getAllServices(): List<Service>

    @Update
    suspend fun updateService(service: Service)

    @Delete
    suspend fun deleteService(service: Service)
}