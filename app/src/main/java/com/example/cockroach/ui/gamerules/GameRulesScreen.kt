package com.example.cockroach.ui.gamerules

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cockroach.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GameRulesScreen(modifier: Modifier = Modifier) {
    var rulesHtml by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current
    // Загружаем правила один раз при старте экрана
    LaunchedEffect(Unit) {
        rulesHtml = try {
            withContext(Dispatchers.IO) { loadRulesFromAssets(context) }
        } catch (_: Exception) {
            getDefaultRulesHtml()
        }
        isLoading = false
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.rules_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(animationSpec = tween(300), initialOffsetY = { 50 }),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                HtmlTextWebView(html = rulesHtml)
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Загрузка правил...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HtmlTextWebView(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                null,
                html,
                "text/html",
                "UTF-8",
                null
            )
        }
    )
}

private fun loadRulesFromAssets(context: Context): String {
    return context.assets.open("game_rules.html").use { inputStream ->
        inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
            reader.readText()
        }
    }
}

private fun getDefaultRulesHtml(): String {
    return """
        <h2>Добро пожаловать в игру "Охота на тараканов"!</h2>
        <p>Правила игры загружаются...</p>
        <p>Если вы видите это сообщение, проверьте наличие файла <b>game_rules.html</b> в папке assets.</p>
    """.trimIndent()
}
