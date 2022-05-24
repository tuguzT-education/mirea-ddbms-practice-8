package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.view.dialog.NewMockDataDialog
import io.github.tuguzt.ddbms.practice8.view.dialog.NewMockUserDialog
import io.github.tuguzt.ddbms.practice8.view.utils.CollectionTable
import io.github.tuguzt.ddbms.practice8.view.utils.OneLineText
import io.github.tuguzt.ddbms.practice8.view.utils.Tooltip
import io.github.tuguzt.ddbms.practice8.viewmodel.MainScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = viewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val selectedCollectionName by viewModel.selectedCollectionName.collectAsState()
    val fieldNames by viewModel.fieldNames.collectAsState()
    val tableRows by viewModel.tableRows.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
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
                onSubmit = { searchText ->
                    val message = when {
                        searchText.isBlank() -> "Nothing to search: all documents are shown"
                        else -> "Searching by \"$searchText\" completed"
                    }
                    coroutineScope.launch {
                        viewModel.search(searchText)
                        snackbarHostState.showSnackbar(message = message, actionLabel = "Dismiss")
                    }
                },
                collectionNames = viewModel.collectionNames,
                onCollectionNameSelected = {
                    val name = requireNotNull(it)
                    coroutineScope.launch { viewModel.selectCollection(name) }
                },
                fieldNames = fieldNames,
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

            if (isDialogOpen) when (selectedCollectionName) {
                MockUser::class.simpleName -> NewMockUserDialog(
                    onCloseRequest = { isDialogOpen = false },
                    onCreateUser = { user ->
                        coroutineScope.launch {
                            viewModel.insertUser(user)
                            snackbarHostState.showSnackbar(
                                message = "Mock user successfully added",
                                actionLabel = "Dismiss",
                            )
                        }
                    },
                )
                MockData::class.simpleName -> NewMockDataDialog(
                    onCloseRequest = { isDialogOpen = false },
                    onCreateData = { data ->
                        coroutineScope.launch {
                            viewModel.insertData(data)
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
        Column(modifier = Modifier.padding(padding)) {
            CollectionTable(columns = fieldNames, rows = tableRows)
        }
    }
}
