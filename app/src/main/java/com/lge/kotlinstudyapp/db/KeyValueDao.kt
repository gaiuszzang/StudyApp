package com.lge.kotlinstudyapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface KeyValueDao {
    @Query("SELECT value FROM KeyValueTable WHERE `key` = (:key)")
    fun getData(key: String): LiveData<String?>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putData(data: KeyValueData)
}