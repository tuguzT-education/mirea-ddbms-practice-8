package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.runtime.*

class DataTableState<T>(selectedItem: T? = null, isLoading: Boolean = false) {
    var selectedItem by mutableStateOf(selectedItem)
    var isLoading by mutableStateOf(isLoading)

    fun clearSelection() {
        selectedItem = null
    }
}

inline fun <T> DataTableState<T>.whenLoading(action: () -> Unit) {
    isLoading = true
    action()
    isLoading = false
}

@Composable
fun <T> rememberDataTableState(
    selectedItem: T? = null,
    isLoading: Boolean = false,
) = remember {
    DataTableState(
        selectedItem = selectedItem,
        isLoading = isLoading,
    )
}
