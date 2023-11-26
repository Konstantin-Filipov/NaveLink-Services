package com.example.myapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Service::class], version = 1)
abstract class ServiceDatabase : RoomDatabase() {
    abstract fun serviceDao() : ServiceDao

}