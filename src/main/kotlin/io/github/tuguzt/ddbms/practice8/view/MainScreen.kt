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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.view.dialog.NewMockDataDialog
import io.github.tuguzt.ddbms.practice8.view.dialog.NewMockUserDialog
import io.github.tuguzt.ddbms.practice8.view.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase
import kotlin.reflect.full.memberProperties

@Composable
fun MainScreen(
    database: CoroutineDatabase,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val userCollection = remember { database.getCollection<MockUser>("mockUser") }
    val dataCollection = remember { database.getCollection<MockData>("mockData") }

    val collectionNames = remember {
        listOf(
            requireNotNull(MockUser::class.simpleName),
            requireNotNull(MockData::class.simpleName),
        )
    }
    var selectedCollection by remember { mutableStateOf(collectionNames.first()) }

    val userCollectionFieldNames = remember { MockUser::class.memberProperties.map { it.name } }
    val dataCollectionFieldNames = remember { MockData::class.memberProperties.map { it.name } }

    var tableRows by remember { mutableStateOf(listOf<List<String>>()) }
    var fieldNames by remember { mutableStateOf(userCollectionFieldNames) }

    suspend fun updateTableRows() {
        when (selectedCollection) {
            MockUser::class.simpleName -> {
                fieldNames = userCollectionFieldNames
                tableRows = userCollection.find().toList().map {
                    listOf(it.name, it.age.toString())
                }
            }
            MockData::class.simpleName -> {
                fieldNames = dataCollectionFieldNames
                tableRows = dataCollection.find().toList().map {
                    listOf(it.data1.toString(), it.data2, it.data3.toString())
                }
            }
        }
    }

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
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Searching by \"$searchText\"...",
                            actionLabel = "Dismiss",
                        )
                    }
                },
                collectionNames = collectionNames,
                onCollectionNameSelected = { name ->
                    selectedCollection = requireNotNull(name)
                    coroutineScope.launch { updateTableRows() }
                },
                fieldNames = fieldNames,
                onFieldNameSelected = { name ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Field \"${name.orEmpty()}\" was chosen",
                            actionLabel = "Dismiss",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            var isDialogOpen by remember { mutableStateOf(false) }

            Tooltip(text = "Insert into collection") {
                ExtendedFloatingActionButton(
                    text = { OneLineText(text = "ADD") },
                    icon = { Icon(
                        painter = painterResource("icons/add.svg"),
                        contentDescription = "Insert into collection",
                    ) },
                    onClick = { isDialogOpen = true },
                )
            }

            if (isDialogOpen) when (selectedCollection) {
                MockUser::class.simpleName -> NewMockUserDialog(
                    onCloseRequest = { isDialogOpen = false },
                    onCreateUser = { user ->
                        coroutineScope.launch {
                            userCollection.insertOne(user)
                            updateTableRows()
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
                            dataCollection.insertOne(data)
                            updateTableRows()
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
        Column(modifier = modifier.padding(padding)) {
            CollectionTable(columns = fieldNames, rows = tableRows)
        }
    }
}

@Composable
private fun TopBar(
    onSubmit: (String) -> Unit,
    collectionNames: List<String>,
    onCollectionNameSelected: (String?) -> Unit,
    fieldNames: List<String>,
    onFieldNameSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colors.primarySurface,
        elevation = AppBarDefaults.TopAppBarElevation,
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            var searchText by remember { mutableStateOf("") }
            // todo "pass search query into MongoDB
            //  and change table content appropriately"

            Row(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Tooltip(text = "Collection name") {
                    TopExposedDropdownMenu(
                        items = collectionNames,
                        dropdownType = "Collection",
                        onItemSelected = onCollectionNameSelected,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Tooltip(text = "Field name") {
                    TopExposedDropdownMenu(
                        items = fieldNames,
                        dropdownType = "Field",
                        onItemSelected = onFieldNameSelected,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Tooltip(text = "Search bar") {
                    TopSearchBar(
                        value = searchText,
                        onSubmit = { onSubmit(searchText) },
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp)),
                    )
                }
            }
        }
    }
}
