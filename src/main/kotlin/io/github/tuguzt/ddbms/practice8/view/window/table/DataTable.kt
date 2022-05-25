package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.OneLineText
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun <T> DataTable(
    modifier: Modifier = Modifier,
    header: TableHeaderScope.() -> Unit,
    content: TableScope<T>.() -> Unit,
) {
    val shape = RoundedCornerShape(4.dp)
    val borderColor = MaterialTheme.colors.onSurface.copy(0.12f)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(.5f.dp, borderColor, shape)
            .clip(shape)
    ) {
        val tableHeaderScope = remember(header) {
            TableHeaderScopeImpl().apply { header() }
        }
        if (tableHeaderScope.isPresent()) {
            Row(
                modifier = Modifier.heightIn(min = 56.dp).background(borderColor),
                verticalAlignment = Alignment.CenterVertically,
                content = { tableHeaderScope.inflate(rowScope = this) },
            )
            Divider()
        }
        // todo "add logic for displaying data loading with usage of LinearProgressIndicator
        //  (it's not immediate, which is seen on app startup"

        var selectedItem: T? by remember { mutableStateOf(null) }

        val tableScope = remember(content) {
            TableScopeImpl(
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
            ).apply {
                content()
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (tableScope.isPresent()) {
                val state = rememberLazyListState()

                LazyColumn(state = state, modifier = Modifier.matchParentSize()) {
                    tableScope.inflate(lazyListScope = this)
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState = state),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                )
            } else {
                EmptyTableBanner()
            }
        }
    }
}

@Preview
@Composable
private fun DataTablePreview() {
    val data = (1..100).mapIndexed { index, _ -> index to "item $index" }

    Practice8Theme {
        Surface {
            DataTable<Pair<Int, String>>(
                header = {
                    column(weight = 1f) { OneLineText(text = "Index") }
                    column(weight = 2f) { OneLineText(text = "Name") }
                }
            ) {
                rows(data) { (index, name) ->
                    column(weight = 1f) { OneLineText(text = "$index") }
                    column(weight = 2f) { OneLineText(text = name) }
                }
            }
        }
    }
}