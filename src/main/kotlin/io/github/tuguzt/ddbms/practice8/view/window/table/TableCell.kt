package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.capitalize
import io.github.tuguzt.ddbms.practice8.view.OneLineText
import io.github.tuguzt.ddbms.practice8.view.Tooltip

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TableCell(
    isHeader: Boolean,
    contentText: String,
    modifier: Modifier = Modifier,
) {
    val text = when (isHeader) {
        false -> contentText
        true -> contentText.capitalize()
    }

    val composable: @Composable () -> Unit = {
        OneLineText(
            text = "$text\n",
            fontWeight = FontWeight.SemiBold.takeIf { isHeader },
        )
    }

    var hover by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .onPointerEvent(PointerEventType.Enter) { hover = true }
        .onPointerEvent(PointerEventType.Exit) { hover = false }
        .weight(1f)
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            when (isHeader) {
                true -> SortIconButton(hover) { composable() }
                false -> SelectionContainer { composable() }
            }
        }
    }
}

@Composable
private fun SortIconButton(
    hover: Boolean,
    content: @Composable () -> Unit,
) {
    var sortOrder by remember { mutableStateOf(0) }

    val (painter, actionDescription) = when (sortOrder) {
        0 -> painterResource("icons/filter_list.svg") to "Sort in ascending order"
        1 -> painterResource("icons/arrow_upward.svg") to "Sort in descending order"
        else -> painterResource("icons/arrow_downward.svg") to "Disable sorting"
    }

    Tooltip(text = actionDescription) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            content()

            if (hover || sortOrder != 0) {
                Spacer(modifier = Modifier.width(8.dp))

                // todo "implement sorting (in both orders), maybe for multiple fields

                IconButton(onClick = {
                    sortOrder.cycleSortOrder().also { sortOrder = it }
                }) {
                    Icon(painter = painter, contentDescription = actionDescription)
                }
            }
        }
    }
}

private fun Int.cycleSortOrder() = when (this) { 0 -> 1; 1 -> -1; else -> 0 }
