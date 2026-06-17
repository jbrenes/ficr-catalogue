package it.ficr.pagaiacronos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import it.ficr.pagaiacronos.ui.navigation.AppNavHost
import it.ficr.pagaiacronos.ui.theme.PagaiaCronosTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PagaiaCronosTheme {
                AppNavHost()
            }
        }
    }
}
