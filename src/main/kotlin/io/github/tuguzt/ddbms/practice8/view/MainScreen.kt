package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
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

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = focusManager::clearFocus,
            ),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Surface(modifier = Modifier.padding(top = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var expanded by remember { mutableStateOf(false) }
                    val suggestions = listOf("Item1", "Item2", "Item3")
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
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
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
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}
