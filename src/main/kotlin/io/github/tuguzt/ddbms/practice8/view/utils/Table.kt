package io.github.tuguzt.ddbms.practice8.view.utils

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TableRow(
    contentList: List<String>,
    modifier: Modifier = Modifier,
    isHeader: Boolean = false,
) {
    var active by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(if (isHeader) 56.dp else 52.dp)
            .onPointerEvent(PointerEventType.Enter) { if (!isHeader) active = true }
            .onPointerEvent(PointerEventType.Exit) { if (!isHeader) active = false }
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
                Text(
                    text = "$text\n",
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold.takeIf { isHeader },
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    if (isHeader) Tooltip(text = text, content = composable)
                    else composable()
                }
            }
        }
    }
    Divider()
}

@Composable
fun CollectionTable(
    columns: List<String>,
    rows: List<List<String>>,
) {
    val shape = RoundedCornerShape(4.dp)
    val borderColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(.5f.dp, borderColor, shape)
            .clip(shape)
    ) {
        TableRow(isHeader = true, contentList = columns)

        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLazyListState()

            SelectionContainer {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                ) {
                    items(rows) {
                        TableRow(contentList = it)
                    }
                }
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
