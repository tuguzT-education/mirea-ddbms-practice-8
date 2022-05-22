package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import io.github.tuguzt.ddbms.practice8.docker.createDockerComposeContainer
import io.github.tuguzt.ddbms.practice8.docker.mongo1Service
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
import io.github.tuguzt.ddbms.practice8.view.theme.isSystemInDarkTheme
import kotlinx.coroutines.*
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.testcontainers.containers.DockerComposeContainer

const val title = "DDBMS Practice 8"

@Composable
@Suppress("NAME_SHADOWING")
fun ApplicationScope.Practice8Application() {
    val coroutineScope = rememberCoroutineScope()

    var isConnecting by remember { mutableStateOf(true) }
    var isInDarkTheme by remember { mutableStateOf(isSystemInDarkTheme()) }

    var container: DockerComposeContainer<*>? by remember { mutableStateOf(null) }
    var containerStartJob: Job? by remember { mutableStateOf(null) }
    var client: CoroutineClient? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        launch {
            while (isActive) {
                val newMode = isSystemInDarkTheme()
                if (isInDarkTheme != newMode) {
                    isInDarkTheme = newMode
                }
                delay(1000)
            }
        }
        containerStartJob = withContext(Dispatchers.IO) {
            container = createDockerComposeContainer()
            launch {
                val container = requireNotNull(container)
                container.start()
                val service = container.mongo1Service()
                client = KMongo.createClient("mongodb://$service").coroutine
            }
        }
        isConnecting = false
    }

    Practice8Theme(darkTheme = isInDarkTheme) {
        when {
            isConnecting -> ConnectingWindow(onCloseRequest = ::exitApplication)
            else -> MainWindow(
                onCloseRequest = ::exitApplication,
                client = requireNotNull(client),
                coroutineScope = coroutineScope,
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.launch(Dispatchers.IO) {
                containerStartJob?.cancelAndJoin()
                client?.close()
                container?.stop()
            }
        }
    }
}

@Composable
private fun ConnectingWindow(onCloseRequest: () -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        title = "$title — Connecting…",
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
private fun MainWindow(
    onCloseRequest: () -> Unit,
    client: CoroutineClient,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    Window(onCloseRequest = onCloseRequest, title = title) {
        val database = client.getDatabase("ddbms-practice-8")
        MainScreen(database, coroutineScope)
    }
}
