package com.mbpatel.weatherinfo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/** room to defined dao for query*/
@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAllHistory(): LiveData<List<History>>

    @Insert
    suspend fun insertHistory(mHistory: History): Long

    @Delete
    suspend fun deleteHistory(mHistory: History)

    @Query("SELECT * FROM history WHERE name LIKE '%' || :keyword || '%'")
    fun searchHistory(keyword: String): LiveData<List<History>>

}