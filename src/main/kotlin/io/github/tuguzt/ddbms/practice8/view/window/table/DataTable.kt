package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.OneLineText
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

private enum class TableDisplayState {
    Loading,
    Showing,
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> DataTable(
    header: TableHeaderScope.() -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    state: DataTableState<T> = rememberDataTableState(),
    content: TableScope<T>.() -> Unit,
) {
    val borderColor = MaterialTheme.colors.onSurface.copy(0.12f)

    Column(
        modifier = modifier
            .border(.5f.dp, borderColor, shape)
            .clip(shape)
    ) {
        val tableHeaderScope = TableHeaderScopeImpl()
        tableHeaderScope.header()
        if (tableHeaderScope.isPresent()) {
            Row(
                modifier = Modifier.heightIn(min = 56.dp).background(borderColor),
                verticalAlignment = Alignment.CenterVertically,
                content = { tableHeaderScope.inflate(rowScope = this) },
            )
            Divider()
        }

        val tableScope = TableScopeImpl(state)
        tableScope.content()

        val displayState = when {
            state.isLoading -> TableDisplayState.Loading
            tableScope.isPresent() -> TableDisplayState.Showing
            else -> null
        }
        AnimatedVisibility(visible = displayState == TableDisplayState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(targetState = displayState) {
                when (it) {
                    TableDisplayState.Showing -> {
                        val listState = rememberLazyListState()
                        LazyColumn(state = listState, modifier = Modifier.matchParentSize()) {
                            tableScope.inflate(lazyListScope = this)
                        }
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(scrollState = listState),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight(),
                        )
                    }
                    TableDisplayState.Loading -> LoadingTableBanner()
                    null -> EmptyTableBanner()
                }
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
                modifier = Modifier.fillMaxSize().padding(16.dp),
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
