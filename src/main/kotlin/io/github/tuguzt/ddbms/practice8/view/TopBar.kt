package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.utils.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.view.utils.SearchBar

@Composable
fun TopExposedDropdownMenu(
    items: List<String>,
    dropdownType: String,
    onItemSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenu(
        items = items,
        expanded = expanded,
        dropdownType = dropdownType,
        onItemSelected = onItemSelected,
        onExpandedChange = { expanded = it },
        modifier = modifier
            .widthIn(max = 192.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
    )
}

@Composable
fun TopSearchBar(
    value: String,
    onSubmit: () -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        value = value,
        modifier = modifier,
        onSubmit = onSubmit,
        singleLine = true,
        onValueChange = onValueChange,
        colors = run {
            val color = MaterialTheme.colors.primarySurface
            val contentColor = contentColorFor(color)
            val focusColor = when {
                MaterialTheme.colors.isLight -> contentColor
                else -> MaterialTheme.colors.primary
            }

            TextFieldDefaults.textFieldColors(
                placeholderColor = contentColor.copy(alpha = ContentAlpha.medium),
                leadingIconColor = contentColor.copy(alpha = TextFieldDefaults.IconOpacity),
                trailingIconColor = contentColor.copy(alpha = TextFieldDefaults.IconOpacity),
                cursorColor = focusColor,
                focusedLabelColor = focusColor.copy(alpha = ContentAlpha.medium),
                unfocusedLabelColor = focusColor.copy(alpha = ContentAlpha.high),
                focusedIndicatorColor = focusColor.copy(alpha = ContentAlpha.medium),
                unfocusedIndicatorColor = focusColor.copy(alpha = ContentAlpha.disabled),
            )
        },
    )
}
