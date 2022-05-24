package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
import io.github.tuguzt.ddbms.practice8.viewmodel.AppViewModel
import io.github.tuguzt.ddbms.practice8.viewmodel.MainScreenViewModel
import kotlinx.coroutines.CoroutineScope
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

@Composable
private fun ConnectingWindow(onCloseRequest: () -> Unit) {
    Window(
        title = "$title — Connecting…",
        resizable = false,
        onCloseRequest = onCloseRequest,
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 400.dp,
            height = 200.dp,
        ),
    ) {
        ConnectingScreen()
    }
}

@Composable
private fun MainWindow(
    onCloseRequest: () -> Unit,
    viewModel: MainScreenViewModel = viewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    Window(onCloseRequest = onCloseRequest, title = title) {
        MainScreen(viewModel = viewModel, coroutineScope = coroutineScope)
    }
}
