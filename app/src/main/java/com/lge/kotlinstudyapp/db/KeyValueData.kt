package com.lge.kotlinstudyapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "KeyValueTable", primaryKeys = ["key"])
data class KeyValueData (val key: String, @ColumnInfo(name = "value") val value: String?)