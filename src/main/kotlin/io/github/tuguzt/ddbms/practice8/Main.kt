package io.github.tuguzt.ddbms.practice8

import androidx.compose.ui.window.application
import io.github.tuguzt.ddbms.practice8.di.appModule
import io.github.tuguzt.ddbms.practice8.di.viewModelModule
import io.github.tuguzt.ddbms.practice8.view.Practice8Application
import org.koin.core.context.startKoin
import org.koin.logger.slf4jLogger

fun main() {
    startKoin {
        slf4jLogger()
        modules(viewModelModule, appModule)
    }
    application {
        Practice8Application()
    }
}
