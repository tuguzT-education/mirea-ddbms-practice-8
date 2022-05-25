package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.Tooltip

@Composable
fun SortIconButton(
    hover: Boolean,
    onSortOrderChanged: (Int) -> Unit,
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

                IconButton(onClick = {
                    sortOrder = sortOrder.cycleSortOrder()
                    onSortOrderChanged(sortOrder)
                }) {
                    Icon(painter = painter, contentDescription = actionDescription)
                }
            }
        }
    }
}

private fun Int.cycleSortOrder() = when (this) { 0 -> 1; 1 -> -1; else -> 0 }
