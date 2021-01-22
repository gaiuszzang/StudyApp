package com.lge.kotlinstudyapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [KeyValueData::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun keyValueDao(): KeyValueDao
}