package com.mbpatel.weatherinfo.db

/**
 * Repository class that works with remote data sources.
 */
class HistoryRepository private constructor(private val historyDao: HistoryDao) {

    fun fetchAllHistory() = historyDao.getAllHistory()
    suspend fun deleteHistory(mHistory: History) {
        historyDao.deleteHistory(mHistory)
    }

    suspend fun saveHistory(
        bLat: Double,
        bLng: Double,
        bName: String
    ) {
//        if (isEdit)
//            bookmarkDao.updateComment(iId, iComment)
//        else {
        val mHistory = History(
            latitude = bLat,
            longitude = bLng,
            name = bName
        )
        historyDao.insertHistory(mHistory)
//        }
    }

    fun searchHistory(keyword: String) = historyDao.searchHistory(keyword)

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(historyDao: HistoryDao) =
            instance ?: synchronized(this) {
                instance ?: HistoryRepository(historyDao).also { instance = it }
            }
    }
}