package io.github.tuguzt.ddbms.practice8.view.utils

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
private fun TableRow(
    isHeader: Boolean,
    contentList: List<String>,
    modifier: Modifier = Modifier,
    active: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(if (isHeader) 56.dp else 52.dp)
            .background(
                when {
                    isHeader -> MaterialTheme.colors.onSurface.copy(
                        alpha = TextFieldDefaults.BackgroundOpacity
                    )
                    active -> MaterialTheme.colors.onSurface.copy(
                        alpha = TextFieldDefaults.BackgroundOpacity / 3
                    )
                    else -> Color.Transparent
                }
            )
    ) {
        for (content in contentList) {
            val text = content.replaceFirstChar { it.uppercase() }

            val composable: @Composable () -> Unit = {
                OneLineText(
                    text = "$text\n",
                    fontWeight = FontWeight.SemiBold.takeIf { isHeader },
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    when (isHeader) {
                        true -> Tooltip(text = text) { composable() }
                        false -> composable()
                    }
                }
            }
        }
    }
    Divider()
}

@Composable
fun ContentRow(
    contentList: List<String>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    SelectionContainer {
        TableRow(
            isHeader = false,
            active = expanded,
            contentList = contentList,
            modifier = modifier.clickable(onClick = { expanded = !expanded })
        )
    }

    var renderCount by remember { mutableStateOf(0) }
    listOf(renderCount, renderCount - 1).forEach { renderId ->
        val isActive = renderId == renderCount
        key(renderId) {
            CursorDropdownMenu(
                expanded = expanded && isActive,
                onDismissRequest = {
                    if (isActive) {
                        renderCount += 1
                        expanded = false
                    }
                },
            ) {
                DropdownMenuItem(onClick = {}) {
                    OneLineText(text = "Update")
                }
                DropdownMenuItem(onClick = {}) {
                    OneLineText(text = "Delete")
                }
            }
        }
    }
}

@Composable
fun CollectionTable(
    columns: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(4.dp)
    val borderColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(.5f.dp, borderColor, shape)
            .clip(shape)
    ) {
        TableRow(isHeader = true, contentList = columns)

        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLazyListState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = state,
            ) {
                items(rows) { ContentRow(it) }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState = state),
            )
        }
    }
}

@Preview
@Composable
fun CollectionTablePreview() {
    val tableData = (1..100).mapIndexed { index, _ -> index to "Item $index" }

    CollectionTable(
        columns = listOf("column 1", "column 2"),
        rows = mutableListOf<List<String>>().apply {
            for (data in tableData)
                add(data.first, listOf(data.first.toString(), data.second))
        }
    )
}
