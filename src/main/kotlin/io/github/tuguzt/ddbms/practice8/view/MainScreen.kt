package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Mock
import io.github.tuguzt.ddbms.practice8.view.utils.CollectionTablePreview
import io.github.tuguzt.ddbms.practice8.view.utils.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.view.utils.SearchBar
import io.github.tuguzt.ddbms.practice8.view.utils.Tooltip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase

@Composable
fun MainScreen(
    database: CoroutineDatabase,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val collection = remember { database.getCollection<Mock>("mock") }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = focusManager::clearFocus,
        ),
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        topBar = {
            TopBar(
                onSubmit = { searchText ->
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Searching by \"$searchText\"",
                            actionLabel = "Dismiss",
                        )
                    }
                },
                dropdownItems = List(5) { "Item ${it + 1}" },
                onDropdownItemSelected = { item ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Item \"${item.orEmpty()}\" was chosen",
                            actionLabel = "Dismiss",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CollectionTablePreview()
        }
    }
}

@Composable
private fun TopBar(
    onSubmit: (String) -> Unit,
    dropdownItems: List<String>,
    onDropdownItemSelected: (String?) -> Unit,
) {
    Surface(
        color = MaterialTheme.colors.primarySurface,
        elevation = AppBarDefaults.TopAppBarElevation,
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            var searchText by remember { mutableStateOf("") }
            var dropdownExpanded by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Tooltip(text = "Collection name", modifier = Modifier.width(128.dp).heightIn(min = 56.dp)) {
                    ExposedDropdownMenu(
                        items = dropdownItems,
                        dropdownType = "collection",
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        onItemSelected = onDropdownItemSelected,
                        modifier = Modifier.width(128.dp).heightIn(min = 56.dp),
                        color = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity),
                        shape = RoundedCornerShape(4.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Tooltip(text = "Search bar", modifier = Modifier.fillMaxWidth()) {
                    TopSearchBar(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                        onSubmit = { onSubmit(searchText) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TopSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        onSubmit = onSubmit,
        singleLine = true,
        colors = run {
            val color = MaterialTheme.colors.primarySurface
            val contentColor = contentColorFor(color)
            val focusColor = when {
                MaterialTheme.colors.isLight -> contentColor
                else -> MaterialTheme.colors.primary
            }

            TextFieldDefaults.textFieldColors(
                placeholderColor = contentColor.copy(alpha = ContentAlpha.medium),
                cursorColor = focusColor,
                focusedIndicatorColor = focusColor.copy(alpha = ContentAlpha.medium),
                unfocusedIndicatorColor = focusColor.copy(alpha = ContentAlpha.disabled),
                leadingIconColor = contentColor.copy(alpha = TextFieldDefaults.IconOpacity),
                trailingIconColor = contentColor.copy(alpha = TextFieldDefaults.IconOpacity),
                focusedLabelColor = focusColor.copy(alpha = ContentAlpha.medium),
                unfocusedLabelColor = focusColor.copy(alpha = ContentAlpha.high),
            )
        },
    )
}
