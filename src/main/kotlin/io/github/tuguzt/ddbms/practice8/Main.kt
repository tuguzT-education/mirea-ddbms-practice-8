package io.github.tuguzt.ddbms.practice8

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.tuguzt.ddbms.practice8.view.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
