package io.github.tuguzt.ddbms.practice8.view.utils

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.heightIn(min = 56.dp),
        placeholder = { Text("Search") },
        singleLine = singleLine,
        trailingIcon = {
            Icon(
                painter = painterResource("icons/search.svg"),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        },
    )
}

@Composable
@Preview
private fun SearchBarPreview() {
    MaterialTheme {
        SearchBar(value = "", onValueChange = {})
    }
}
