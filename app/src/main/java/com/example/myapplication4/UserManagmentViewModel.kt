package com.example.myapplication4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication4.data.User
import com.example.myapplication4.data.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserManagementViewModel(private val userDao: UserDao) : ViewModel() {
    val users: Flow<List<User>> = userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addUser(user: User) = viewModelScope.launch {
        userDao.insertUser(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        userDao.updateUser(user)
    }

    fun deleteUser(userId: Long) = viewModelScope.launch {
        userDao.getUserById(userId.toInt())?.let { userDao.deleteUser(it) }
    }
}

class UserManagementViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserManagementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserManagementViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
