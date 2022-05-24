package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun DataTable(
    columns: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(4.dp)
    val borderColor = MaterialTheme.colors.onSurface.copy(TextFieldDefaults.BackgroundOpacity)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(.5f.dp, borderColor, shape)
            .clip(shape)
    ) {
        TableRow(isHeader = true, contentList = columns)
        // todo "add logic for displaying data loading with usage of LinearProgressIndicator
        //  (it's not immediate, which is seen on app startup"

        Box(modifier = Modifier.fillMaxSize()) {
            if (rows.flatten().isEmpty()) {
                EmptyTableBanner()
            } else {
                val state = rememberLazyListState()

                LazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(rows) { ContentRow(it) }
                }
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState = state),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun DataTablePreview() {
    val tableData = (1..100).mapIndexed { index, _ -> index to "item $index" }

    Practice8Theme {
        Surface {
            DataTable(
                columns = listOf("column 1", "column 2"),
                rows = tableData.map { (index, name) -> listOf(index.toString(), name) },
            )
        }
    }
}
