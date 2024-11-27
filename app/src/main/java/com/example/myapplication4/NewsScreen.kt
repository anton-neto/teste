package com.example.myapplication4

import Article
import NYTimesResponse
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    var articles by mutableStateOf<List<Article>>(listOf())
    var isLoading by mutableStateOf(false)
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> get() = _errorMessage

    fun fetchArticles(apiKey: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response: NYTimesResponse = RetrofitInstance.api.getTopStories(apiKey)
                articles = response.results
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar as notícias"
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun NewsScreen(navController: NavController, userName: String) {
    val newsViewModel: NewsViewModel = viewModel()
    val errorMessage = newsViewModel.errorMessage.value
    val articles = newsViewModel.articles
    val isLoading = newsViewModel.isLoading

    Surface(
        color = Color(0xFFF5F5F5), // Fundo cinza claro
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabeçalho fixo
            item {
                Text(
                    text = "Bem-vindo, $userName!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Gerencie notícias e usuários abaixo",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.navigate("user_management") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A73E8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Gerenciar Usuários")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { navController.navigate("news_crud") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A73E8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Gerenciar Notícias")
                    }
                }

                Button(
                    onClick = { newsViewModel.fetchArticles("nMSQiDweZ2AGZthXwh9yIGO9bkY3tp6v") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A73E8),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Carregar Notícias")
                }
            }

            // Lista de notícias
            items(articles) { article ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    )
                    article.abstract?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF666666)
                            )
                        )
                    }
                    Button(
                        onClick = {
                            shareArticle(navController, article.title, article.url)
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A73E8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Compartilhar")
                    }
                }
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFFCCCCCC)
                )
            }
        }

        // Exibição de mensagens de erro
        errorMessage?.let {
            Toast.makeText(navController.context, it, Toast.LENGTH_SHORT).show()
        }
    }
}

fun shareArticle(navController: NavController, title: String, url: String) {
    val context = navController.context
    val shareIntent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        putExtra(android.content.Intent.EXTRA_TEXT, "Confira esta notícia: $title\n$url")
        type = "text/plain"
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartilhar via"))
}
