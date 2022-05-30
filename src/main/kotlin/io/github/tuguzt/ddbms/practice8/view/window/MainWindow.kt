package io.github.tuguzt.ddbms.practice8.view.window

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import io.github.tuguzt.ddbms.practice8.capitalize
import io.github.tuguzt.ddbms.practice8.model.Identifiable
import io.github.tuguzt.ddbms.practice8.view.OneLineText
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.CreateDialog
import io.github.tuguzt.ddbms.practice8.view.dialog.DeleteDialog
import io.github.tuguzt.ddbms.practice8.view.dialog.UpdateDialog
import io.github.tuguzt.ddbms.practice8.view.title
import io.github.tuguzt.ddbms.practice8.view.viewModel
import io.github.tuguzt.ddbms.practice8.view.window.table.DataTable
import io.github.tuguzt.ddbms.practice8.view.window.table.rememberDataTableState
import io.github.tuguzt.ddbms.practice8.view.window.table.rows
import io.github.tuguzt.ddbms.practice8.view.window.table.whenLoading
import io.github.tuguzt.ddbms.practice8.view.window.topbar.TopBar
import io.github.tuguzt.ddbms.practice8.viewmodel.MainScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

@Composable
fun MainWindow(
    onCloseRequest: () -> Unit,
    viewModel: MainScreenViewModel = viewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    Window(onCloseRequest = onCloseRequest, title = title) {
        MainScreen(viewModel = viewModel, coroutineScope = coroutineScope)
    }
}

private val logger = KotlinLogging.logger {}

@Composable
private fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val selectedCollection by viewModel.selectedCollectionClass.collectAsState()
    val fields by viewModel.fields.collectAsState()
    val tableRows by viewModel.tableRows.collectAsState()

    var searchText by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val tableState = rememberDataTableState<Identifiable<*>>()
    val focusManager = LocalFocusManager.current

    val onSuccessBuild: suspend (String) -> Unit = {
        snackbarHostState.showSnackbar(
            message = it,
            actionLabel = "Dismiss",
        )
    }

    val onFailure: suspend (Throwable) -> Unit = {
        logger.error(it) { "Something went wrong" }
        snackbarHostState.showSnackbar(
            message = "Something went wrong...",
            actionLabel = "Dismiss",
        )
    }

    suspend fun handleSearch() {
        val action: suspend () -> Result<Unit> = {
            tableState.whenLoading {
                runCatching { viewModel.search(searchText) }
            }
        }
        val onSuccess: suspend () -> Unit = {
            val message = when {
                searchText.isBlank() -> "Nothing to search: all documents are shown"
                else -> "Searching by \"$searchText\" completed"
            }
            onSuccessBuild(message)
        }
        handleError(action, onSuccess, onFailure)
    }

    suspend fun handleInsert(item: Identifiable<*>) {
        val action: suspend () -> Result<Unit> = {
            tableState.whenLoading {
                runCatching { viewModel.insert(item) }
            }
        }
        val onSuccess: suspend () -> Unit = {
            onSuccessBuild("${item::class.simpleName} successfully added")
        }
        handleError(action, onSuccess, onFailure)
    }

    suspend fun handleSortByField(field: KProperty1<out Identifiable<*>, *>, sortOrder: Int) {
        val action: suspend () -> Result<Unit> = {
            tableState.whenLoading {
                runCatching { viewModel.sortByField(field.name, searchText) }
            }
        }
        val onSuccess: suspend () -> Unit = {
            val orderDescription = when (sortOrder) {
                1 -> "ascending"
                -1 -> "descending"
                else -> "no"
            }
            onSuccessBuild("Sorting data by \"${field.name}\" field in $orderDescription order")
        }
        handleError(action, onSuccess, onFailure)
    }

    suspend fun handleUpdate(item: Identifiable<*>, onCloseRequest: () -> Unit) {
        val action: suspend () -> Result<Unit> = {
            tableState.whenLoading {
                runCatching { viewModel.update(item) }.apply { onCloseRequest() }
            }
        }
        val onSuccess: suspend () -> Unit = {
            onSuccessBuild("${item::class.simpleName} successfully updated")
        }
        handleError(action, onSuccess, onFailure)
    }

    suspend fun handleDelete(item: Identifiable<*>, onCloseRequest: () -> Unit) {
        val action: suspend () -> Result<Unit> = {
            tableState.whenLoading {
                runCatching { viewModel.delete(item) }.apply { onCloseRequest() }
            }
        }
        val onSuccess: suspend () -> Unit = {
            onSuccessBuild("${item::class.simpleName} successfully deleted")
        }
        handleError(action, onSuccess, onFailure)
    }

    Scaffold(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = focusManager::clearFocus,
        ),
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        topBar = {
            TopBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onSubmitSearch = {
                    coroutineScope.launch { handleSearch() }
                },
                collectionNames = viewModel.collectionClasses.map { requireNotNull(it.simpleName) },
                onCollectionNameSelected = {
                    coroutineScope.launch { viewModel.selectCollection(requireNotNull(it)) }
                },
                fieldNames = fields.map { it.name },
                onFieldNameSelected = {
                    coroutineScope.launch { viewModel.selectField(requireNotNull(it)) }
                },
            )
        },
        floatingActionButton = {
            var isDialogOpen by remember { mutableStateOf(false) }

            Tooltip(text = "Insert into collection") {
                ExtendedFloatingActionButton(
                    text = { OneLineText(text = "ADD") },
                    icon = {
                        Icon(
                            painter = painterResource("icons/add.svg"),
                            contentDescription = "Insert into collection",
                        )
                    },
                    onClick = { isDialogOpen = true },
                )
            }

            if (isDialogOpen) CreateDialog(
                kClass = selectedCollection,
                onApplyToItem = { coroutineScope.launch { handleInsert(it) } },
                onCloseRequest = { isDialogOpen = false },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            var dropdownExpanded by remember { mutableStateOf(false) }

            DataTable(
                state = tableState,
                header = {
                    fields.forEach { field ->
                        column(
                            content = { OneLineText(text = field.name.capitalize()) },
                            onSortOrderChanged = {
                                coroutineScope.launch { handleSortByField(field, it) }
                            },
                        )
                    }
                },
                modifier = Modifier.matchParentSize().padding(16.dp),
            ) {
                rows(
                    items = tableRows,
                    content = { item ->
                        item::class.memberProperties
                            .filter { !it.name.contains("id", ignoreCase = true) }
                            .forEach {
                                column { OneLineText(text = it.getter.call(item).toString()) }
                            }
                    },
                    onItemSelected = { dropdownExpanded = !dropdownExpanded },
                )
            }

            var isUpdateDialogOpen by remember { mutableStateOf(false) }
            var isDeleteDialogOpen by remember { mutableStateOf(false) }

            var renderCount by remember { mutableStateOf(0) }
            listOf(renderCount, renderCount - 1).forEach { renderId ->
                val isActive = renderId == renderCount
                key(renderId) {
                    CursorDropdownMenu(
                        expanded = dropdownExpanded && isActive,
                        onDismissRequest = {
                            if (isActive) {
                                renderCount += 1
                                dropdownExpanded = false
                                tableState.clearSelection()
                            }
                        },
                    ) {
                        DropdownMenuItem(onClick = { isUpdateDialogOpen = true }) {
                            OneLineText(text = "Update")
                        }
                        DropdownMenuItem(onClick = { isDeleteDialogOpen = true }) {
                            OneLineText(text = "Delete")
                        }
                    }
                }
            }

            val onCloseRequest = {
                dropdownExpanded = false
                isUpdateDialogOpen = false
                isDeleteDialogOpen = false
                tableState.clearSelection()
            }

            if (isUpdateDialogOpen) UpdateDialog(
                identifiable = requireNotNull(tableState.selectedItem),
                onApplyToItem = { coroutineScope.launch { handleUpdate(it, onCloseRequest) } },
                onCloseRequest = onCloseRequest,
            )
            if (isDeleteDialogOpen) {
                val selected = requireNotNull(tableState.selectedItem)
                DeleteDialog(
                    title = "${selected::class.simpleName}",
                    onCancel = onCloseRequest,
                    onConfirm = { coroutineScope.launch { handleDelete(selected, onCloseRequest) } },
                )
            }
        }
    }
}

private suspend inline fun handleError(
    crossinline action: suspend () -> Result<Unit>,
    crossinline onSuccess: suspend () -> Unit,
    crossinline onFailure: suspend (Throwable) -> Unit,
) {
    val result = action()
    when {
        result.isSuccess -> onSuccess()
        else -> onFailure(result.exceptionOrNull()!!)
    }
}
