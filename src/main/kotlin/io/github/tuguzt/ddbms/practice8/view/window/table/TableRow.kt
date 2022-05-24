package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.OneLineText

@Composable
fun TableRow(
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
        modifier = modifier.clickable {
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
