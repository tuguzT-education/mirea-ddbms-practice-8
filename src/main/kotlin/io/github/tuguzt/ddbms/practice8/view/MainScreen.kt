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
import io.github.tuguzt.ddbms.practice8.view.utils.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.view.utils.SearchBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase

@Composable
fun MainScreen(
    database: CoroutineDatabase,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val collection = remember { database.getCollection<Mock>("mock") }
    var searchQuery by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = focusManager::clearFocus,
            ),
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        topBar = {
            val color = MaterialTheme.colors.primarySurface
            val contentColor = contentColorFor(color)
            Surface(
                color = color,
                contentColor = contentColor,
                elevation = AppBarDefaults.TopAppBarElevation,
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    var expanded by remember { mutableStateOf(false) }
                    val suggestions = listOf("Item1", "Item2", "Item3")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ExposedDropdownMenu(
                            items = suggestions,
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            onItemSelected = { item ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Item \"${item.orEmpty()}\" was chosen",
                                        actionLabel = "Dismiss",
                                    )
                                }
                            },
                            modifier = Modifier
                                .width(128.dp)
                                .heightIn(min = 56.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(4.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SearchBar(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp)),
                            onSubmit = {
                                focusManager.clearFocus()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Searching by query \"$searchQuery\"",
                                        actionLabel = "Dismiss",
                                    )
                                }
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                placeholderColor = contentColor.copy(alpha = ContentAlpha.medium),
                                focusedIndicatorColor = contentColor.copy(alpha = ContentAlpha.medium),
                                unfocusedIndicatorColor = contentColor.copy(alpha = ContentAlpha.disabled),
                                leadingIconColor = contentColor.copy(alpha = TextFieldDefaults.IconOpacity),
                                trailingIconColor = contentColor.copy(alpha = TextFieldDefaults.IconOpacity),
                                focusedLabelColor = contentColor.copy(alpha = ContentAlpha.medium),
                                unfocusedLabelColor = contentColor.copy(alpha = ContentAlpha.high),
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {}
    }
}
