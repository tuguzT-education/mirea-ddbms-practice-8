package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp

@LayoutScopeMarker
interface TableHeaderScope {
    fun column(
        weight: Float = 1f,
        onSortOrderChanged: ((Int) -> Unit)? = null,
        content: @Composable () -> Unit,
    )
}

class TableHeaderScopeImpl : TableHeaderScope {
    private val columns = mutableListOf<@Composable RowScope.() -> Unit>()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun column(
        weight: Float,
        onSortOrderChanged: ((Int) -> Unit)?,
        content: @Composable () -> Unit,
    ) {
        columns += {
            var hover by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .onPointerEvent(PointerEventType.Enter) { hover = true }
                    .onPointerEvent(PointerEventType.Exit) { hover = false }
                    .weight(weight)
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    when (onSortOrderChanged) {
                        null -> content()
                        else -> SortIconButton(hover, onSortOrderChanged, content)
                    }
                }
            }
        }
    }

    fun isPresent(): Boolean = columns.isNotEmpty()

    @Composable
    fun inflate(rowScope: RowScope) {
        columns.forEach { rowScope.it() }
        columns.clear()
    }
}
