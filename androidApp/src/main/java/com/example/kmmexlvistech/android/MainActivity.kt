package com.example.kmmexlvistech.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import com.example.kmmexlvistech.App
import com.example.kmmexlvistech.AppContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Same logic as in App(): later this can come from your saved ThemeMode
            val darkTheme = isSystemInDarkTheme()

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT,
                        detectDarkMode = { darkTheme },
                    ),
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT,
                        detectDarkMode = { darkTheme },
                    ),
                )
                onDispose { }
            }

            App(context = AppContext)
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}