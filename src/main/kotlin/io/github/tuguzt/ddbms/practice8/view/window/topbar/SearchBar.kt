package io.github.tuguzt.ddbms.practice8.view.window.topbar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.OneLineText

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    value: String,
    onSubmit: () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
) {
    TextField(
        value = value,
        colors = colors,
        singleLine = singleLine,
        onValueChange = onValueChange,
        placeholder = { OneLineText(text = "Search") },
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
        trailingIcon = {
            IconButton(onClick = onSubmit) {
                Icon(
                    painter = painterResource("icons/search.svg"),
                    contentDescription = "Search field",
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
            onSubmit = {},
            onValueChange = {},
        )
    }
}
