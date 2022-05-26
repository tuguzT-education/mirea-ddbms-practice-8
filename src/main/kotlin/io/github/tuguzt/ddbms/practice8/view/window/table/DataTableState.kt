package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.runtime.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class DataTableState<T>(selectedItem: T? = null, isLoading: Boolean = false) {
    var selectedItem by mutableStateOf(selectedItem)
    var isLoading by mutableStateOf(isLoading)

    fun clearSelection() {
        selectedItem = null
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T, R> DataTableState<T>.whenLoading(action: () -> R): R {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    isLoading = true
    val result = action()
    isLoading = false
    return result
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
