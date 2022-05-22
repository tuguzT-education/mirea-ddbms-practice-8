package io.github.tuguzt.ddbms.practice8.view.utils

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun ExposedDropdownMenu(
    items: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onElementSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.surface,
) {
    var selectedText by remember {
        val selection = items.firstOrNull()
        onElementSelected(selection)
        mutableStateOf(selection ?: "Nothing to select")
    }

    Surface(
        modifier = modifier.clickable { onExpandedChange(!expanded) },
        color = color,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedText,
                    style = MaterialTheme.typography.button,
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
                    DropdownMenuItem(
                        onClick = {
                            if (selectedText != it) {
                                selectedText = it
                                onElementSelected(selectedText)
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

@Composable
@Preview
private fun ExposedDropdownMenuPreview() {
    Practice8Theme {
        ExposedDropdownMenu(
            items = listOf("Hello World"),
            expanded = true,
            onExpandedChange = {},
            onElementSelected = {},
            modifier = Modifier.requiredWidth(256.dp),
        )
    }
}
