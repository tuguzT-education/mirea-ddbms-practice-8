package io.github.tuguzt.ddbms.practice8.view.utils

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
import java.util.*

@Composable
fun ExposedDropdownMenu(
    items: List<String>,
    dropdownType: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.surface,
    shape: Shape = RectangleShape,
) {
    var selectedText by remember {
        val selection = items.firstOrNull()
        onItemSelected(selection)
        mutableStateOf(selection ?: "Nothing to select")
    }

    Surface(color = color, shape = shape) {
        Column {
            Row(
                modifier = modifier
                    .clip(shape)
                    .clickable { onExpandedChange(!expanded) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedText,
                    modifier = Modifier.weight(1f),
                )

                val (painter, contentDescription) = when {
                    expanded -> painterResource("icons/arrow_drop_up.svg") to "Expand menu"
                    else -> painterResource("icons/arrow_drop_down.svg") to "Hide menu"
                }
                Icon(painter, contentDescription)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
            ) {
                items.forEach {
                    Tooltip(text = "\"${it.lowercase(Locale.getDefault())}\" $dropdownType") {
                        DropdownMenuItem(
                            onClick = {
                                if (selectedText != it) {
                                    selectedText = it
                                    onItemSelected(selectedText)
                                }
                                onExpandedChange(false)
                            },
                        ) {
                            Text(text = it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun ExposedDropdownMenuPreview() {
    Practice8Theme {
        ExposedDropdownMenu(
            items = listOf("Hello World"),
            dropdownType = "example",
            expanded = false,
            onExpandedChange = {},
            onItemSelected = {},
            modifier = Modifier.requiredWidth(256.dp),
        )
    }
}
