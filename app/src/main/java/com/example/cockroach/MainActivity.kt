package com.example.cockroach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.cockroach.ui.registration.RegistrationScreen
import com.example.cockroach.ui.theme.CockroachTheme

/**
 * Главная Activity приложения
 * В Android Activity - это экран приложения
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Включает отображение контента под системными барами

        setContent {
            CockroachTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Запускаем экран регистрации
                    RegistrationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}