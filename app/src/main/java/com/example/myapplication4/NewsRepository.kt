package com.example.myapplication4.data

class NewsRepository(private val newsDao: NewsDao) {

    suspend fun addNews(news: News) {
        newsDao.insert(news)
    }

    suspend fun markAsInteresting(news: News, isInteresting: Boolean) {
        val updatedNews = news.copy(isInteresting = isInteresting)
        newsDao.updateNews(updatedNews)
    }

    suspend fun getAllNews(): List<News> {
        return newsDao.getAllNews()
    }

    suspend fun getInterestingNews(): List<News> {
        return newsDao.getInterestingNews()
    }
}
