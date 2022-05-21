package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import io.github.tuguzt.ddbms.practice8.docker.createDockerComposeContainer
import kotlinx.coroutines.*
import org.testcontainers.containers.DockerComposeContainer

const val title = "DDBMS Practice 8"

@Composable
fun ApplicationScope.Practice8Application() {
    val coroutineScope = rememberCoroutineScope()

    var isConnecting by remember { mutableStateOf(true) }
    var container: DockerComposeContainer<*>? by remember { mutableStateOf(null) }
    var containerStartJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        containerStartJob = withContext(Dispatchers.IO) {
            container = createDockerComposeContainer()
            launch { requireNotNull(container).start() }
        }
        isConnecting = false
    }

    when {
        isConnecting -> ConnectingWindow(onCloseRequest = ::exitApplication)
        else -> MainWindow(onCloseRequest = ::exitApplication)
    }

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.launch {
                requireNotNull(containerStartJob).cancelAndJoin()
                requireNotNull(container).stop()
            }
        }
    }
}

@Composable
private fun ConnectingWindow(onCloseRequest: () -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        title = "$title - Connecting...",
        resizable = false,
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
private fun MainWindow(onCloseRequest: () -> Unit) {
    Window(onCloseRequest = onCloseRequest, title = title) {
        MainScreen()
    }
}
