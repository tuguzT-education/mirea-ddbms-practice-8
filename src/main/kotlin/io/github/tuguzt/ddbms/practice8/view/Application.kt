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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineClient

const val title = "DDBMS Practice 8"

@Composable
@Suppress("NAME_SHADOWING")
fun ApplicationScope.Practice8Application() {
    val appViewModel by viewModel<AppViewModel>()
    val darkTheme by appViewModel.darkTheme.collectAsState()
    val client by appViewModel.client.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Practice8Theme(darkTheme = darkTheme) {
        when (val client = client) {
            null -> ConnectingWindow(onCloseRequest = ::exitApplication)
            else -> MainWindow(
                client = client,
                coroutineScope = coroutineScope,
                onCloseRequest = ::exitApplication,
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.launch(Dispatchers.IO) {
                appViewModel.close()
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
    client: CoroutineClient,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val database = remember(client) { client.getDatabase("ddbms-practice-8") }

    Window(onCloseRequest = onCloseRequest, title = title) {
        MainScreen(
            database = database,
            coroutineScope = coroutineScope,
        )
    }
}
