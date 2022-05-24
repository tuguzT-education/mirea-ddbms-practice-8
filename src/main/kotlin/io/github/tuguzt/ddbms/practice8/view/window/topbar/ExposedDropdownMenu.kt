package io.github.tuguzt.ddbms.practice8.view.window.topbar

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
import io.github.tuguzt.ddbms.practice8.capitalize
import io.github.tuguzt.ddbms.practice8.view.OneLineText
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun ExposedDropdownMenu(
    expanded: Boolean,
    items: List<String>,
    dropdownType: String,
    onItemSelected: (String?) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.surface,
    shape: Shape = RectangleShape,
) {
    var selectedText by remember(items) {
        val selection = items.firstOrNull()
        onItemSelected(selection)
        mutableStateOf(selection ?: "Nothing to select")
    }

    Surface(color = color, shape = shape) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .clip(shape)
                    .clickable { onExpandedChange(!expanded) },
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                OneLineText(
                    text = selectedText.capitalize(),
                    modifier = Modifier.weight(1f),
                )

                val (painter, contentDescription) = when {
                    expanded -> painterResource("icons/arrow_drop_up.svg") to "Expand menu"
                    else -> painterResource("icons/arrow_drop_down.svg") to "Hide menu"
                }
                Icon(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(48.dp),
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
            ) {
                items.forEach {
                    Tooltip(text = "$dropdownType \"${it.lowercase()}\"") {
                        DropdownMenuItem(
                            onClick = {
                                if (selectedText != it) {
                                    selectedText = it
                                    onItemSelected(selectedText)
                                }
                                onExpandedChange(false)
                            },
                        ) {
                            OneLineText(text = it.capitalize())
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
            expanded = false,
            dropdownType = "example",
            onItemSelected = {},
            onExpandedChange = {},
            modifier = Modifier.requiredWidth(256.dp),
        )
    }
}
