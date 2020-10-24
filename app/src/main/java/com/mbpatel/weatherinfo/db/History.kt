package com.mbpatel.weatherinfo.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="history")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long =0,
    val latitude:Double,
    val longitude:Double,
    val name:String
){}