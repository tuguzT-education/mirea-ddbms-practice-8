package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.runtime.*

class DataTableState<T> {
    var selectedItem: T? by mutableStateOf(null)

    fun clearSelection() {
        selectedItem = null
    }
}

@Composable
fun <T> rememberDataTableState() = remember { DataTableState<T>() }
