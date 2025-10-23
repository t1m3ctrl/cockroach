package com.example.cockroach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cockroach.data.model.GameSettings
import com.example.cockroach.ui.authors.AuthorsScreen
import com.example.cockroach.ui.game.GameScreen
import com.example.cockroach.ui.game.GameViewModel
import com.example.cockroach.ui.gamerules.GameRulesScreen
import com.example.cockroach.ui.records.RecordsScreen
import com.example.cockroach.ui.registration.RegistrationScreen
import com.example.cockroach.ui.registration.RegistrationViewModel
import com.example.cockroach.ui.settings.GameSettingsScreen
import com.example.cockroach.ui.theme.CockroachTheme
import com.example.cockroach.ui.theme.Green40

/**
 * Главная Activity приложения
 * В Android Activity - это экран приложения
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

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
    var gameSettings by remember { mutableStateOf(GameSettings()) }
    val gameViewModel: GameViewModel = viewModel()
    val registrationViewModel: RegistrationViewModel = viewModel()

    // Получаем текущего игрока из RegistrationViewModel
    val currentPlayer by registrationViewModel.currentPlayer.collectAsState()

//    val tabs = listOf("Регистрация", "Игра", "Правила игры", "Рекорды", "Авторы", "Настройки")
    val tabs = listOf("\uD83D\uDC64", "\uD83C\uDFAE", "\uD83D\uDCCB", "\uD83C\uDFC6", "\uD83D\uDC65", "⚙")

    // Передаем текущего игрока в GameViewModel и обновляем настройки при смене игрока
    LaunchedEffect(currentPlayer) {
        gameViewModel.setCurrentPlayer(currentPlayer)
        // Обновляем настройки игры в соответствии с уровнем сложности игрока
        currentPlayer?.let { player ->
            gameSettings = player.difficultyLevel.toGameSettings()
        } ?: run {
            // Если игрок не выбран, используем настройки по умолчанию
            gameSettings = GameSettings()
        }
    }

    // Автоматическая пауза при смене вкладки
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex != 1) { // Не вкладка игры
            gameViewModel.pauseGame()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex,
            containerColor = Green40) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> RegistrationScreen(viewModel = registrationViewModel)
            1 -> GameScreen(
                gameSettings = gameSettings,
                viewModel = gameViewModel
            )
            2 -> GameRulesScreen()
            3 -> RecordsScreen()
            4 -> AuthorsScreen()
            5 -> key(currentPlayer?.id ?: 0L) { // Принудительная рекомпозиция при смене игрока
                GameSettingsScreen(
                    currentSettings = gameSettings,
                    onSettingsChanged = { settings ->
                        gameSettings = settings
                    }
                )
            }
        }
    }
}