package com.example.myapplication4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication4.data.News
import com.example.myapplication4.data.NewsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class NewsManagementViewModel(private val newsDao: NewsDao) : ViewModel() {
    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList.asStateFlow()

    init {
        loadAllNews()
    }

    fun addNews(news: News) {
        viewModelScope.launch(Dispatchers.IO) {
            newsDao.insert(news)
            loadAllNews()
        }
    }

    fun updateNews(news: News) {
        viewModelScope.launch(Dispatchers.IO) {
            newsDao.updateNews(news)
            loadAllNews()
        }
    }

    fun deleteNews(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val news = newsList.value.find { it.id == id }
            if (news != null) newsDao.deleteNews(news)
            loadAllNews()
        }
    }

    private fun loadAllNews() {
        viewModelScope.launch(Dispatchers.IO) {
            val news = newsDao.getAllNews()
            _newsList.value = news
        }
    }
}

class NewsManagementViewModelFactory(private val newsDao: NewsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsManagementViewModel::class.java)) {
            return NewsManagementViewModel(newsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
