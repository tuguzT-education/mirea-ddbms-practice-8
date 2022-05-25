package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@LayoutScopeMarker
interface TableScope<T> {
    fun row(
        item: T,
        onItemSelected: (() -> Unit)? = null,
        content: @Composable TableRowScope.() -> Unit,
    )

    fun clearSelection()
}

fun <T> TableScope<T>.rows(
    items: List<T>,
    onItemSelected: ((T) -> Unit)? = null,
    content: @Composable TableRowScope.(T) -> Unit,
) {
    items.forEach { item ->
        val onItemSelectedErased = onItemSelected?.let { { it(item) } }
        row(item, onItemSelected = onItemSelectedErased) { content(item) }
    }
}

class TableScopeImpl<T>(
    private val selectedItem: T? = null,
    private val onSelectedItemChange: (T?) -> Unit,
) : TableScope<T> {
    private val rows = mutableListOf<@Composable () -> Unit>()

    override fun row(
        item: T,
        onItemSelected: (() -> Unit)?,
        content: @Composable (TableRowScope.() -> Unit),
    ) {
        rows += {
            val tableRowScope = TableRowScopeImpl()
            tableRowScope.content()
            if (tableRowScope.isPresent()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .heightIn(min = 52.dp)
                        .clickable {
                            onSelectedItemChange(item)
                            onItemSelected?.invoke()
                        }
                        .background(
                            when (selectedItem) {
                                item -> MaterialTheme.colors.onSurface.copy(alpha = 0.04f)
                                else -> Color.Transparent
                            }
                        ),
                    content = { tableRowScope.inflate(rowScope = this) },
                )
                Divider()
            }
        }
    }

    override fun clearSelection() {
        onSelectedItemChange(null)
    }

    fun isPresent(): Boolean = rows.isNotEmpty()

    fun inflate(lazyListScope: LazyListScope): Unit = rows.forEach { lazyListScope.item { it() } }
}
