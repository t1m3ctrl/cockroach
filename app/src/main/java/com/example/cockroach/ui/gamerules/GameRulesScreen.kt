package com.example.cockroach.ui.gamerules

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun GameRulesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Загружаем правила синхронно
    val rulesHtml = remember {
        try {
            loadRulesFromAssets(context)
        } catch (_: Exception) {
            getDefaultRulesHtml()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            HtmlTextWebView(html = rulesHtml)
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
    return context.assets.open("rules.html").use { inputStream ->
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
