package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@LayoutScopeMarker
interface TableRowScope {
    fun column(weight: Float = 1f, content: @Composable () -> Unit)
}

class TableRowScopeImpl : TableRowScope {
    private val columns = mutableListOf<@Composable RowScope.() -> Unit>()

    override fun column(weight: Float, content: @Composable () -> Unit) {
        columns += {
            Box(modifier = Modifier.weight(weight)) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    content = { content() },
                )
            }
        }
    }

    fun isPresent(): Boolean = columns.isNotEmpty()

    @Composable
    fun inflate(rowScope: RowScope): Unit = columns.forEach { rowScope.it() }
}
