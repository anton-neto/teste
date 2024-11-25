package com.example.myapplication4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.*
import com.example.myapplication4.data.AppDatabase
import com.example.myapplication4.data.News
import com.example.myapplication4.data.NewsDao
import com.example.myapplication4.data.NewsRepository
import com.example.myapplication4.data.User
import com.example.myapplication4.data.UserDao
import com.example.myapplication4.ui.theme.MyApplication4Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obter instâncias do banco de dados e DAOs
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()
        val newsDao = db.newsDao()

        // Testes de CRUD
        testUserCRUD(userDao)
        testNewsCRUD(newsDao)

        setContent {
            MyApplication4Theme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "registration") {
                    composable("registration") {
                        UserRegistrationScreen(navController = navController)
                    }
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    composable("news/{userName}") { backStackEntry ->
                        val userName = backStackEntry.arguments?.getString("userName") ?: "Usuário"
                        NewsScreen(navController = navController, userName = userName)
                    }
                    composable("user_management") {
                        UserManagementScreen(userDao = userDao, navController = navController)
                    }
                }
            }
        }
    }

    private fun testUserCRUD(userDao: UserDao) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Operações CRUD de usuários
                val user1 = User(name = "TESTE", email = "teste@example.com", password = "1234")
                val existingUser1 = userDao.getUserByEmail(user1.email)
                if (existingUser1 != null) {
                    Log.d("UserCRUD", "Usuário com email ${user1.email} já existe.")
                } else {
                    userDao.insertUser(user1)
                    Log.d("UserCRUD", "Usuário inserido: $user1")
                }
                val retrievedUser1 = userDao.getUserByName("TESTE")
                Log.d("UserCRUD", "Usuário encontrado: $retrievedUser1")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("UserCRUD", "Erro ao realizar operações de CRUD: ${e.message}")
                }
            }
        }
    }

    private fun testNewsCRUD(newsDao: NewsDao) {
        val newsRepository = NewsRepository(newsDao)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Operações CRUD de notícias
                val news1 = News(
                    title = "Notícia 1",
                    description = "Descrição da Notícia 1",
                    url = "https://example.com/1"
                )
                newsRepository.addNews(news1)
                Log.d("NewsCRUD", "Notícia inserida: $news1")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("NewsCRUD", "Erro ao realizar operações de CRUD: ${e.message}")
                }
            }
        }
    }
}
