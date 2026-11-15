package com.rfm.quran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rfm.quran.ui.AboutScreen
import com.rfm.quran.ui.AudioScreen
import com.rfm.quran.ui.HomeScreen
import com.rfm.quran.ui.ReaderScreen
import com.rfm.quran.ui.TafsirScreen
import com.rfm.quran.ui.TasbihScreen
import com.rfm.quran.ui.theme.QuranTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuranTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(nav) }
        composable("reader") { ReaderScreen(onBack = { nav.popBackStack() }) }
        composable("tafsir") { TafsirScreen(onBack = { nav.popBackStack() }) }
        composable("audio") { AudioScreen(onBack = { nav.popBackStack() }) }
        composable("tasbih") { TasbihScreen(onBack = { nav.popBackStack() }) }
        composable("about") { AboutScreen(onBack = { nav.popBackStack() }) }
    }
}
