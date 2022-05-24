package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.docker.createDockerComposeContainer
import io.github.tuguzt.ddbms.practice8.docker.mongo1Service
import io.github.tuguzt.ddbms.practice8.view.theme.isSystemInDarkTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.testcontainers.containers.DockerComposeContainer
import kotlin.time.Duration.Companion.seconds

class AppViewModel(viewModelScope: CoroutineScope) : ViewModel(viewModelScope) {
    private val _darkTheme = MutableStateFlow(isSystemInDarkTheme())
    val darkTheme = _darkTheme.asStateFlow()

    private val darkThemeWatcher = viewModelScope.launch {
        while (isActive) {
            val newMode = isSystemInDarkTheme()
            _darkTheme.compareAndSet(expect = !newMode, update = newMode)
            delay(1.seconds)
        }
    }

    private var _container: DockerComposeContainer<*>? = null
    private var _containerStartJob: Job? = null

    private var _client: MutableStateFlow<CoroutineClient?> = MutableStateFlow(null)
    val client = _client.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _container = createDockerComposeContainer()
            _containerStartJob = launch {
                val container = requireNotNull(_container)
                container.start()

                val service = container.mongo1Service()
                val client = KMongo.createClient("mongodb://$service").coroutine
                _client.emit(value = client)
            }
        }
    }

    suspend fun close() {
        darkThemeWatcher.cancelAndJoin()
        _containerStartJob?.cancelAndJoin()
        withContext(Dispatchers.IO) {
            _client.value?.close()
            _container?.stop()
        }
    }
}
