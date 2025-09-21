package com.example.cockroach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.cockroach.ui.authors.AuthorsScreen
import com.example.cockroach.ui.gamerules.GameRulesScreen
import com.example.cockroach.ui.registration.RegistrationScreen
import com.example.cockroach.ui.settings.GameSettingsScreen
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
                    MainScreenWithTabs(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreenWithTabs(modifier: Modifier = Modifier) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf("Регистрация", "Правила игры", "Авторы", "Настройки")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> RegistrationScreen()
            1 -> GameRulesScreen()
            2 -> AuthorsScreen()
            3 -> GameSettingsScreen()
        }
    }
}