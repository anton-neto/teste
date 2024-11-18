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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel

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
    // Usando viewModel() para garantir que o estado seja mantido entre rotações
    val newsViewModel: NewsViewModel = viewModel()
    val errorMessage = newsViewModel.errorMessage.value
    val articles = newsViewModel.articles
    val isLoading = newsViewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Logado como $userName",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
        )

        Text(
            text = "Veja as Principais Notícias",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { newsViewModel.fetchArticles("nMSQiDweZ2AGZthXwh9yIGO9bkY3tp6v") },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Buscar Notícias")
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(articles) { article ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = article.title, style = MaterialTheme.typography.titleMedium)
                        article.abstract?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { navController.navigate("article_details/${article.url}") },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(text = "Adicionar na lista")
                            }

                            Button(
                                onClick = { shareArticle(navController, article.title, article.url) },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text(text = "Compartilhar")
                            }
                        }
                    }
                }
            }
        }

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
