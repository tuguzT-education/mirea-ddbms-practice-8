package io.github.tuguzt.ddbms.practice8.view.utils

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .heightIn(min = 56.dp)
            .onPreviewKeyEvent {
                when {
                    it.key == Key.Enter && it.type == KeyEventType.KeyUp -> {
                        onSubmit()
                        true
                    }
                    else -> false
                }
            },
        placeholder = { Text("Search") },
        colors = colors,
        singleLine = singleLine,
        trailingIcon = {
            IconButton(onClick = onSubmit) {
                Icon(
                    painter = painterResource("icons/search.svg"),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    )
}

@Composable
@Preview
private fun SearchBarPreview() {
    MaterialTheme {
        SearchBar(
            value = "",
            onValueChange = {},
            onSubmit = {},
        )
    }
}
