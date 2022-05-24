package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.runtime.*
import androidx.compose.ui.window.ApplicationScope
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
import io.github.tuguzt.ddbms.practice8.view.window.ConnectingWindow
import io.github.tuguzt.ddbms.practice8.view.window.MainWindow
import io.github.tuguzt.ddbms.practice8.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val title = "DDBMS Practice 8"

@Composable
fun ApplicationScope.Practice8Application(viewModel: AppViewModel = viewModel()) {
    val darkTheme by viewModel.darkTheme.collectAsState()
    val client by viewModel.client.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Practice8Theme(darkTheme = darkTheme) {
        when (client) {
            null -> ConnectingWindow(onCloseRequest = ::exitApplication)
            else -> MainWindow(
                coroutineScope = coroutineScope,
                onCloseRequest = ::exitApplication,
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.launch(Dispatchers.IO) {
                viewModel.close()
            }
        }
    }
}
