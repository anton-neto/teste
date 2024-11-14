package com.example.myapplication4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import com.example.myapplication4.ui.theme.MyApplication4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}
