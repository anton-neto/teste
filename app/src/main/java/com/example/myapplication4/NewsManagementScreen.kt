package com.example.myapplication4

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication4.data.News
import com.example.myapplication4.data.NewsDao
import kotlinx.coroutines.launch

@Composable
fun NewsManagementScreen(
    newsDao: NewsDao,
    navController: NavController
) {
    val viewModel: NewsManagementViewModel = viewModel(factory = NewsManagementViewModelFactory(newsDao))
    val newsList by viewModel.newsList.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var isMain by remember { mutableStateOf(false) }
    var isInteresting by remember { mutableStateOf(false) }
    var newsId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Gerenciar Notícias", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxWidth().padding(8.dp)) {
                    if (title.isEmpty()) Text(text = "Título")
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxWidth().padding(8.dp)) {
                    if (description.isEmpty()) Text(text = "Descrição")
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = url,
            onValueChange = { url = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxWidth().padding(8.dp)) {
                    if (url.isEmpty()) Text(text = "URL")
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri)
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isMain, onCheckedChange = { isMain = it })
                Text(text = "É principal?")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isInteresting, onCheckedChange = { isInteresting = it })
                Text(text = "É interessante?")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                coroutineScope.launch {
                    if (newsId == null) {
                        viewModel.addNews(
                            News(
                                title = title,
                                description = description,
                                url = url,
                                isMain = isMain,
                                isInteresting = isInteresting
                            )
                        )
                    } else {
                        newsId?.let {
                            viewModel.updateNews(
                                News(
                                    id = it,
                                    title = title,
                                    description = description,
                                    url = url,
                                    isMain = isMain,
                                    isInteresting = isInteresting
                                )
                            )
                        }
                    }
                }
            }) {
                Text(text = if (newsId == null) "Adicionar" else "Atualizar")
            }

            Button(onClick = {
                coroutineScope.launch {
                    newsId?.let { viewModel.deleteNews(it) }
                }
            }, enabled = newsId != null) {
                Text(text = "Deletar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Notícias Cadastradas", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        newsList.forEach { news ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "ID: ${news.id}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Título: ${news.title}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Descrição: ${news.description}", style = MaterialTheme.typography.bodyMedium)

                // Adicionando um espaçamento para separar a descrição do botão "Editar"
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        title = news.title
                        description = news.description
                        url = news.url
                        isMain = news.isMain
                        isInteresting = news.isInteresting
                        newsId = news.id
                    },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(text = "Editar")
                }
            }
        }
    }
}
