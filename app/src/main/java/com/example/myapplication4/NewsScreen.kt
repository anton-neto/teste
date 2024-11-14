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

@Composable
fun NewsScreen(navController: NavController, userName: String) {
    var articles by remember { mutableStateOf<List<Article>>(listOf()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    fun fetchArticles() {
        isLoading = true
        coroutineScope.launch {
            try {
                val apiKey = "nMSQiDweZ2AGZthXwh9yIGO9bkY3tp6v"
                val response: NYTimesResponse = RetrofitInstance.api.getTopStories(apiKey)


                articles = response.results
            } catch (e: Exception) {
                Toast.makeText(navController.context, "Erro ao carregar as notícias", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }


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
            onClick = { fetchArticles() },
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


                        Button(
                            onClick = { navController.navigate("article_details/${article.url}") },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(text = "Adicionar na lista")
                        }
                    }
                }
            }
        }
    }
}

