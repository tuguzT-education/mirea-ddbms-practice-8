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
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.view.OneLineText
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.create.CreateMockDataDialog
import io.github.tuguzt.ddbms.practice8.view.dialog.create.CreateMockUserDialog
import io.github.tuguzt.ddbms.practice8.view.dialog.DeleteDialog
import io.github.tuguzt.ddbms.practice8.view.dialog.update.UpdateMockDataDialog
import io.github.tuguzt.ddbms.practice8.view.dialog.update.UpdateMockUserDialog
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
                    val message = when {
                        searchText.isBlank() -> "Nothing to search: all documents are shown"
                        else -> "Searching by \"$searchText\" completed"
                    }
                    coroutineScope.launch {
                        tableState.whenLoading {
                            viewModel.search(searchText)
                        }
                        snackbarHostState.showSnackbar(message = message, actionLabel = "Dismiss")
                    }
                },
                collectionNames = viewModel.collectionClasses.map { requireNotNull(it.simpleName) },
                onCollectionNameSelected = {
                    val name = requireNotNull(it)
                    coroutineScope.launch { viewModel.selectCollection(name) }
                },
                fieldNames = fields.map { it.name },
                onFieldNameSelected = {
                    val name = requireNotNull(it)
                    coroutineScope.launch { viewModel.selectField(name) }
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

            if (isDialogOpen) when (selectedCollection) {
                MockUser::class -> CreateMockUserDialog(
                    onCloseRequest = { isDialogOpen = false },
                    onCreateUser = { user ->
                        coroutineScope.launch {
                            tableState.whenLoading {
                                viewModel.insert(user)
                            }
                            snackbarHostState.showSnackbar(
                                message = "Mock user successfully added",
                                actionLabel = "Dismiss",
                            )
                        }
                    },
                )
                MockData::class -> CreateMockDataDialog(
                    onCloseRequest = { isDialogOpen = false },
                    onCreateData = { data ->
                        coroutineScope.launch {
                            tableState.whenLoading {
                                viewModel.insert(data)
                            }
                            snackbarHostState.showSnackbar(
                                message = "Mock data successfully added",
                                actionLabel = "Dismiss",
                            )
                        }
                    }
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            var dropdownExpanded by remember { mutableStateOf(false) }

            DataTable(
                modifier = Modifier
                    .matchParentSize()
                    .padding(16.dp),
                state = tableState,
                header = {
                    fields.forEach { property ->
                        val onSortOrderChanged: (Int) -> Unit = {
                            val orderDescription = when (it) {
                                1 -> "ascending"
                                -1 -> "descending"
                                else -> "no"
                            }
                            val message = "Sorting data by \"${property.name}\" field in $orderDescription order"
                            coroutineScope.launch {
                                tableState.whenLoading {
                                    viewModel.sortByField(property.name, searchText)
                                }
                                snackbarHostState.showSnackbar(message = message, actionLabel = "Dismiss")
                            }
                        }
                        column(
                            onSortOrderChanged = onSortOrderChanged,
                            content = { OneLineText(text = property.name.capitalize()) },
                        )
                    }
                },
            ) {
                rows(
                    items = tableRows,
                    onItemSelected = { dropdownExpanded = !dropdownExpanded },
                    content = {
                        when (it) {
                            is MockUser -> {
                                column { OneLineText(text = it.name) }
                                column { OneLineText(text = "${it.age}") }
                            }
                            is MockData -> {
                                column { OneLineText(text = "${it.data1}") }
                                column { OneLineText(text = it.data2) }
                                column { OneLineText(text = "${it.data3}") }
                            }
                        }
                    },
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
                        DropdownMenuItem(
                            onClick = { isUpdateDialogOpen = true }
                        ) {
                            OneLineText(text = "Update")
                        }
                        DropdownMenuItem(
                            onClick = { isDeleteDialogOpen = true }
                        ) {
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

            if (isUpdateDialogOpen) when (val selected = requireNotNull(tableState.selectedItem)) {
                is MockUser -> UpdateMockUserDialog(
                    onCloseRequest = onCloseRequest,
                    user = selected,
                    onUpdateUser = { user ->
                        coroutineScope.launch {
                            tableState.whenLoading {
                                viewModel.update(user)
                                onCloseRequest()
                            }
                            snackbarHostState.showSnackbar(
                                message = "Mock user successfully updated",
                                actionLabel = "Dismiss",
                            )
                        }
                    },
                )
                is MockData -> UpdateMockDataDialog(
                    onCloseRequest = onCloseRequest,
                    data = selected,
                    onUpdateData = { data ->
                        coroutineScope.launch {
                            tableState.whenLoading {
                                viewModel.update(data)
                                onCloseRequest()
                            }
                            snackbarHostState.showSnackbar(
                                message = "Mock data successfully updated",
                                actionLabel = "Dismiss",
                            )
                        }
                    }
                )
            }

            if (isDeleteDialogOpen) when (val selected = tableState.selectedItem) {
                is MockUser -> DeleteMockUserDialog(
                    onCancel = onCloseRequest,
                    onConfirm = {
                        coroutineScope.launch {
                            tableState.whenLoading {
                                viewModel.deleteUser(selected)
                                onCloseRequest()
                            }
                            snackbarHostState.showSnackbar(
                                message = "Mock user successfully deleted",
                                actionLabel = "Dismiss",
                            )
                        }
                    },
                )
//                is MockData -> UpdateDialog(
//                    onCloseRequest = onCloseRequest,
//                    item = selected,
//                    onUpdateItem = { item ->
//                        coroutineScope.launch {
//                            viewModel.update(item)
//                            onCloseRequest()
//                            snackbarHostState.showSnackbar(
//                                message = "${item::class.simpleName} successfully updated",
//                                actionLabel = "Dismiss",
//                            )
//                        }
//                    }
//                )
            }

            if (isDeleteDialogOpen) {
                val selected = requireNotNull(tableState.selectedItem)
                val className = selected::class.simpleName
                DeleteDialog(
                    title = "$className",
                    onCancel = onCloseRequest,
                    onConfirm = {
                        coroutineScope.launch {
                            onCloseRequest()
                            tableState.whenLoading {
                                viewModel.delete(selected)
                                onCloseRequest()
                            }
                            snackbarHostState.showSnackbar(
                                message = "$className successfully deleted",
                                actionLabel = "Dismiss",
                            )
                        }
                    },
                )
            }
        }
    }
}
