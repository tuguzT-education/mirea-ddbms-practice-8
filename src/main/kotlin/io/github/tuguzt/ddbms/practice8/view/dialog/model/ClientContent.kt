package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Booking
import io.github.tuguzt.ddbms.practice8.model.Client
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.viewModel
import io.github.tuguzt.ddbms.practice8.view.window.topbar.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.viewmodel.BookingViewModel
import kotlinx.coroutines.launch

@Composable
fun ClientContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToClient: (Client) -> Unit,
    client: Client? = null,
    bookingViewModel: BookingViewModel = viewModel(),
) {
    var sex by remember { mutableStateOf(client?.sex ?: "") }
    var name by remember { mutableStateOf(client?.name ?: "") }
    var surname by remember { mutableStateOf(client?.surname ?: "") }
    var age by remember { mutableStateOf(if (client != null) "${client.age}" else "") }

    var booking: Booking? by remember { mutableStateOf(null) }

    var items: List<Booking> by remember { mutableStateOf(listOf()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            items = bookingViewModel.getAll()
        }
    }

    @Suppress("NAME_SHADOWING")
    val onClickConfirm = {
        if (client == null) {
            onCloseRequest()
            val client = Client(
                age = age.toInt(),
                sex = sex,
                name = name,
                surname = surname,
                bookingId = requireNotNull(booking?.id),
            )
            onApplyToClient(client)
        } else {
            val client = Client(
                age = age.toInt(),
                sex = sex,
                name = name,
                surname = surname,
                bookingId = requireNotNull(booking?.id),
                id = client.id,
            )
            onApplyToClient(client)
        }
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (client == null) {
            sex.isNotBlank() && name.isNotBlank() && surname.isNotBlank() &&
                    age.toIntOrNull() != null && booking != null
        } else run {
            val age = age.toIntOrNull() ?: return@run false
            val booking = booking ?: return@run false
            sex != client.sex || name != client.name || surname != client.surname ||
                    age != client.age || booking.id != client.bookingId
        }

    TextFieldContainer(
        actionText = actionText,
        enabledConfirm = enabledConfirm,
        onCloseRequest = onCloseRequest,
        onClickConfirm = onClickConfirm,
    ) {
        Tooltip(text = "Input field for \"name\"") {
            OutlinedSingleLineTextField(
                text = "Name",
                value = name,
                onValueChange = { name = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"surname\"") {
            OutlinedSingleLineTextField(
                text = "Surname",
                value = surname,
                onValueChange = { surname = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"age\"") {
            OutlinedSingleLineTextField(
                text = "Age",
                value = age,
                onValueChange = { age = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"sex\"") {
            OutlinedSingleLineTextField(
                text = "Sex",
                value = sex,
                onValueChange = { sex = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Booking", style = MaterialTheme.typography.h6)
        Tooltip(text = "Input field for \"booking\"") {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenu(
                expanded = expanded,
                items = items.map { it.description },
                dropdownType = "Booking",
                onItemSelected = { item -> booking = items.find { it.description == item } },
                onExpandedChange = { expanded = it },
                shape = MaterialTheme.shapes.medium,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
