package io.github.povec.appnav.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.povec.appnav.sample.ui.theme.AppNavTheme
import io.github.povec.appnav.sample.ui.navigation.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavTheme {
                AppNav(
                    onFinish = {},
                )
            }
        }
    }
}