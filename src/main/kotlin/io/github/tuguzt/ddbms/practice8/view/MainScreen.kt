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
import io.github.tuguzt.ddbms.practice8.view.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase
import kotlin.reflect.full.memberProperties

@Composable
fun MainScreen(
    database: CoroutineDatabase,
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
    val userCollectionFieldNames = remember { MockUser::class.memberProperties.map { it.name } }
    val dataCollectionFieldNames = remember { MockData::class.memberProperties.map { it.name } }
    var fieldNames by remember { mutableStateOf(userCollectionFieldNames) }

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
                collectionNames = collectionNames,
                onCollectionNameSelected = { name ->
                    when (name) {
                        MockUser::class.simpleName -> fieldNames = userCollectionFieldNames
                        MockData::class.simpleName -> fieldNames = dataCollectionFieldNames
                    }
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
            ExtendedFloatingActionButton(
                text = { OneLineText(text = "ADD") },
                icon = {
                    Icon(
                        painter = painterResource("icons/add.svg"),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "FAB clicked",
                            actionLabel = "Dismiss",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CollectionTable(
                columns = fieldNames,
                rows = remember(fieldNames) {
                    List(size = 50) {
                        fieldNames.map { name -> "$name ${it + 1}" }
                    }
                },
            )
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
) {
    Surface(
        color = MaterialTheme.colors.primarySurface,
        elevation = AppBarDefaults.TopAppBarElevation,
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            var searchText by remember { mutableStateOf("") }
            var collectionsExpanded by remember { mutableStateOf(false) }
            var fieldsExpanded by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val dropdownColor = MaterialTheme.colors.onSurface.copy(alpha = TextFieldDefaults.BackgroundOpacity)

                Tooltip(text = "Collection name") {
                    ExposedDropdownMenu(
                        items = collectionNames,
                        dropdownType = "Collection",
                        expanded = collectionsExpanded,
                        onExpandedChange = { collectionsExpanded = it },
                        onItemSelected = onCollectionNameSelected,
                        modifier = Modifier.widthIn(max = 192.dp).heightIn(min = 56.dp),
                        color = dropdownColor,
                        shape = RoundedCornerShape(4.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Tooltip(text = "Field name") {
                    ExposedDropdownMenu(
                        items = fieldNames,
                        dropdownType = "Field",
                        expanded = fieldsExpanded,
                        onExpandedChange = { fieldsExpanded = it },
                        onItemSelected = onFieldNameSelected,
                        modifier = Modifier.widthIn(max = 192.dp).heightIn(min = 56.dp),
                        color = dropdownColor,
                        shape = RoundedCornerShape(4.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Tooltip(text = "Search bar") {
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
