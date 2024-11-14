package com.example.myapplication4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.*
import com.example.myapplication4.data.AppDatabase
import com.example.myapplication4.data.News
import com.example.myapplication4.data.NewsRepository
import com.example.myapplication4.data.User
import com.example.myapplication4.ui.theme.MyApplication4Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Testando CRUD de usuário no console de log
        testUserCRUD()
        testNewsCRUD()

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
                }
            }
        }
    }

    private fun testUserCRUD() {
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Inserindo o primeiro usuário com nome "TESTE"
                val user1 = User(name = "TESTE", email = "teste@example.com", password = "1234")
                val existingUser1 = userDao.getUserByEmail(user1.email)
                if (existingUser1 != null) {
                    Log.d("UserCRUD", "Usuário com email ${user1.email} já existe.")
                } else {
                    userDao.insertUser(user1)
                    Log.d("UserCRUD", "Usuário inserido: $user1")
                }

                // 2. Consultando o usuário "TESTE"
                val retrievedUser1 = userDao.getUserByName("TESTE")
                Log.d("UserCRUD", "Usuário encontrado: $retrievedUser1")

                // 3. Atualizando o usuário "TESTE" para "TESTE2"
                retrievedUser1?.let {
                    val updatedUser = it.copy(name = "TESTE2")
                    userDao.updateUser(updatedUser)
                    Log.d("UserCRUD", "Usuário atualizado para: $updatedUser")
                }

                // 4. Consultando o usuário "TESTE2" após a atualização
                val retrievedUser2 = userDao.getUserByName("TESTE2")
                Log.d("UserCRUD", "Usuário atualizado encontrado: $retrievedUser2")

                // 5. Listando todos os usuários
                val users = userDao.getAllUsers().first()
                Log.d("UserCRUD", "Lista de usuários: $users")

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("UserCRUD", "Erro ao realizar operações de CRUD: ${e.message}")
                }
            }
        }
    }

    private fun testNewsCRUD() {
        val db = AppDatabase.getDatabase(this)
        val newsDao = db.newsDao()
        val newsRepository = NewsRepository(newsDao)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Inserindo uma notícia
                val news1 = News(title = "Notícia 1", description = "Descrição da Notícia 1", url = "https://example.com/1")
                newsRepository.addNews(news1)
                Log.d("NewsCRUD", "Notícia inserida: $news1")

                // 2. Inserindo outra notícia
                val news2 = News(title = "Notícia 2", description = "Descrição da Notícia 2", url = "https://example.com/2", isInteresting = true)
                newsRepository.addNews(news2)
                Log.d("NewsCRUD", "Notícia inserida: $news2")

                // 3. Consultando todas as notícias
                val allNews = newsRepository.getAllNews()
                Log.d("NewsCRUD", "Todas as notícias: $allNews")

                // 4. Consultando as notícias interessantes
                val interestingNews = newsRepository.getInterestingNews()
                Log.d("NewsCRUD", "Notícias interessantes: $interestingNews")

                // 5. Atualizando uma notícia
                val newsToUpdate = allNews.firstOrNull() // Pegando a primeira notícia para atualizar
                newsToUpdate?.let {
                    val updatedNews = it.copy(title = "Notícia 1 Atualizada")
                    newsRepository.addNews(updatedNews)
                    Log.d("NewsCRUD", "Notícia atualizada: $updatedNews")
                }

                // 6. Deletando uma notícia
                val newsToDelete = allNews.lastOrNull() // Pegando a última notícia para deletar
                newsToDelete?.let {
                    newsRepository.addNews(it.copy(title = "Notícia deletada"))
                    Log.d("NewsCRUD", "Notícia deletada: $it")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("NewsCRUD", "Erro ao realizar operações de CRUD: ${e.message}")
                }
            }
        }
    }
}
