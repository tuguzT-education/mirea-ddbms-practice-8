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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun RowScope.TableCell(
    isHeader: Boolean,
    contentText: String,
) {
    val text = when (isHeader) {
        false -> contentText
        true -> contentText.replaceFirstChar { it.uppercase() }
    }

    val composable: @Composable () -> Unit = {
        OneLineText(
            text = "$text\n",
            fontWeight = FontWeight.SemiBold.takeIf { isHeader },
        )
    }

    // todo "change Text to TextField on double click
    //  to edit if TableCell is not in header row"

    // todo "add sorting (in both orders) button on hover
    //  if TableCell is in header row"

    Box(modifier = Modifier.weight(1f)) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            when (isHeader) {
                true -> Tooltip(text = text) { composable() }
                false -> SelectionContainer { composable() }
            }
        }
    }
}

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
        for (content in contentList)
            TableCell(isHeader = isHeader, contentText = content)
    }
    Divider()
}

@Composable
fun ContentRow(
    contentList: List<String>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    TableRow(
        active = expanded,
        isHeader = false,
        contentList = contentList,
        modifier = modifier.clickable() {
            // todo "open Dialog with data from row to edit or delete document"
            expanded = !expanded
        }
    )

    var renderCount by remember { mutableStateOf(0) }
    listOf(renderCount, renderCount - 1).forEach { renderId ->
        val isActive = renderId == renderCount
        key(renderId) {
            // todo "somehow get rid of weird "copy/paste" context menu elements,
            //  use this code as an example for custom context menu"
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
        // todo "add LinearProgressIndicator on data loading
        //  (it's not immediate, which is seen on app startup"

        Box(modifier = Modifier.fillMaxSize()) {
            if (rows.flatten().isEmpty()) {
                EmptyTableBanner()
            }
            else {
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

@Composable
private fun EmptyTableBanner() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Icon(
            painter = painterResource("icons/document.svg"),
            contentDescription = "There is no data in this table",
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))

        OneLineText(
            text = "Table is empty",
            fontSize = MaterialTheme.typography.h6.fontSize,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            text = "There is no data in this table yet." +
                    "\nYou can add new document at any time!"
        )
    }
}

@Preview
@Composable
fun CollectionTablePreview() {
    val tableData = (1..100).mapIndexed { index, _ -> index to "item $index" }

    Practice8Theme {
        CollectionTable(
            columns = listOf("column 1", "column 2"),
            rows = tableData.map { (index, name) -> listOf(index.toString(), name) },
        )
    }
}
