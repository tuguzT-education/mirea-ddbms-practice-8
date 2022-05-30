package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Order
import io.github.tuguzt.ddbms.practice8.model.User
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.viewModel
import io.github.tuguzt.ddbms.practice8.view.window.topbar.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun OrderContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToOrder: (Order) -> Unit,
    order: Order? = null,
    userViewModel: UserViewModel = viewModel(),
) {
    var user: User? by remember { mutableStateOf(null) }

    var items: List<User> by remember { mutableStateOf(listOf()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            items = userViewModel.getAll()
            user = order?.userId?.let { id -> items.find { it.id == id } }
        }
    }

    @Suppress("NAME_SHADOWING")
    val onClickConfirm = {
        if (order == null) {
            onCloseRequest()
            val order = Order(
                userId = requireNotNull(user?.id),
                items = listOf(), // todo
            )
            onApplyToOrder(order)
        } else {
            val order = Order(
                userId = requireNotNull(user?.id),
                items = listOf(), // todo
                id = order.id,
            )
            onApplyToOrder(order)
        }
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (order == null) user != null
        else user?.id != order.userId

    TextFieldContainer(
        actionText = actionText,
        enabledConfirm = enabledConfirm,
        onCloseRequest = onCloseRequest,
        onClickConfirm = onClickConfirm,
    ) {
        Text(text = "User", style = MaterialTheme.typography.h6)
        Tooltip(text = "Input field for \"user\"") {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenu(
                expanded = expanded,
                items = items.map { it.name },
                dropdownType = "User",
                onItemSelected = { item -> user = items.find { it.name == item } },
                onExpandedChange = { expanded = it },
                shape = MaterialTheme.shapes.medium,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // todo items
    }
}
