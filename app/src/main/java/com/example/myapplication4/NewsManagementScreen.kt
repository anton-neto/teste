package com.example.myapplication4

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication4.data.News
import com.example.myapplication4.data.NewsDao
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

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

    Surface(
        color = Color(0xFFF5F5F5), // Fundo cinza claro
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Botão de navegação para voltar
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF1A73E8)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gerenciar Notícias",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White, shape = MaterialTheme.shapes.small)
                            .padding(16.dp)
                    ) {
                        if (title.isEmpty()) Text(text = "Título", color = Color(0xFF666666))
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
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White, shape = MaterialTheme.shapes.small)
                            .padding(16.dp)
                    ) {
                        if (description.isEmpty()) Text(text = "Descrição", color = Color(0xFF666666))
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
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White, shape = MaterialTheme.shapes.small)
                            .padding(16.dp)
                    ) {
                        if (url.isEmpty()) Text(text = "URL", color = Color(0xFF666666))
                        innerTextField()
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri)
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isMain, onCheckedChange = { isMain = it })
                    Text(text = "É principal?", color = Color(0xFF666666))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isInteresting, onCheckedChange = { isInteresting = it })
                    Text(text = "É interessante?", color = Color(0xFF666666))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A73E8),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = if (newsId == null) "Adicionar" else "Atualizar")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            newsId?.let { viewModel.deleteNews(it) }
                        }
                    },
                    enabled = newsId != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Deletar")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Notícias Cadastradas",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            newsList.forEach { news ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .background(Color.White, shape = MaterialTheme.shapes.small)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ID: ${news.id}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF666666)
                        )
                    )
                    Text(
                        text = "Título: ${news.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    )
                    Text(
                        text = "Descrição: ${news.description}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF666666)
                        )
                    )
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
                        modifier = Modifier.align(Alignment.Start),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A73E8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Editar")
                    }
                }
            }
        }
    }
}
