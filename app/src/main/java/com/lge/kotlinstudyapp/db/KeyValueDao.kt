package com.lge.kotlinstudyapp.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KeyValueDao {
    @Query("SELECT value FROM KeyValueTable WHERE `key` = (:key)")
    fun getData(key: String): Flow<String?>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putData(data: KeyValueData)
}