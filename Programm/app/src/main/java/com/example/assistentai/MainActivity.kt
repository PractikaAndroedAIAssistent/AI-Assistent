package com.example.assistentai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.assistentai.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import ru.studentai.core.designsystem.theme.StudentAiTheme
import ru.studentai.core.navigation.navigator.Navigator

@AndroidEntryPoint
public class MainActivity : ComponentActivity() {

    @Inject
    public lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentAiTheme {
                AppNavGraph(navigator = navigator)
            }
        }
    }
}
