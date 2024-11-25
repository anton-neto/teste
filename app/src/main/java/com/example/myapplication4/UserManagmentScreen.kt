package com.example.myapplication4

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication4.data.User
import com.example.myapplication4.data.UserDao
import kotlinx.coroutines.launch

@Composable
fun UserManagementScreen(
    userDao: UserDao,
    navController: NavController
) {
    val viewModel: UserManagementViewModel = viewModel(factory = UserManagementViewModelFactory(userDao))
    val users by viewModel.users.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Gerenciar Usuários", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Formulário de entrada
        UserInputField(label = "Nome", value = name, onValueChange = { name = it })
        UserInputField(label = "Email", value = email, onValueChange = { email = it }, keyboardType = KeyboardType.Email)
        UserInputField(label = "Senha", value = password, onValueChange = { password = it }, keyboardType = KeyboardType.Password)

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de ação
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                coroutineScope.launch {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        if (userId == null) {
                            viewModel.addUser(User(name = name, email = email, password = password))
                            Toast.makeText(navController.context, "Usuário adicionado!", Toast.LENGTH_SHORT).show()
                        } else {
                            userId?.let {
                                viewModel.updateUser(User(id = it, name = name, email = email, password = password))
                                Toast.makeText(navController.context, "Usuário atualizado!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(navController.context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text(text = if (userId == null) "Adicionar" else "Atualizar")
            }

            Button(onClick = {
                coroutineScope.launch {
                    userId?.let {
                        viewModel.deleteUser(it)
                        Toast.makeText(navController.context, "Usuário deletado!", Toast.LENGTH_SHORT).show()
                    }
                }
            }, enabled = userId != null) {
                Text(text = "Deletar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de usuários
        Text(text = "Usuários Cadastrados", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        users.forEach { user ->
            UserRow(
                user = user,
                onEditClick = {
                    name = user.name
                    email = user.email
                    password = user.password
                    userId = user.id
                }
            )
        }
    }
}

@Composable
fun UserInputField(label: String, value: String, onValueChange: (String) -> Unit, keyboardType: KeyboardType = KeyboardType.Text) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    if (value.isEmpty()) Text(text = label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType)
        )
    }
}

@Composable
fun UserRow(user: User, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "ID: ${user.id}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Nome: ${user.name}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
        }
        Button(onClick = onEditClick) {
            Text(text = "Editar")
        }
    }
}


