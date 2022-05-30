package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Booking
import io.github.tuguzt.ddbms.practice8.model.Room
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.viewModel
import io.github.tuguzt.ddbms.practice8.view.window.topbar.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.viewmodel.RoomViewModel
import kotlinx.coroutines.launch

@Composable
fun BookingContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToBooking: (Booking) -> Unit,
    booking: Booking? = null,
    roomViewModel: RoomViewModel = viewModel(),
) {
    var description by remember { mutableStateOf(booking?.description ?: "") }
    var guestCount by remember { mutableStateOf(if (booking != null) "${booking.guestCount}" else "") }
    var room: Room? by remember { mutableStateOf(null) }

    var items: List<Room> by remember { mutableStateOf(listOf()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            items = roomViewModel.getAll()
        }
    }

    @Suppress("NAME_SHADOWING")
    val onClickConfirm = {
        if (booking == null) {
            onCloseRequest()
            val booking = Booking(
                guestCount = guestCount.toInt(),
                description = description,
                roomId = requireNotNull(room?.id),
            )
            onApplyToBooking(booking)
        } else {
            val booking = Booking(
                description = description,
                guestCount = guestCount.toInt(),
                roomId = requireNotNull(room?.id),
                id = booking.id,
            )
            onApplyToBooking(booking)
        }
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (booking == null) {
            description.isNotBlank() && guestCount.toIntOrNull() != null && room != null
        } else run {
            val guestCount = guestCount.toIntOrNull() ?: return@run false
            val room = room ?: return@run false
            description != booking.description ||
                    guestCount != booking.guestCount || room.id != booking.roomId
        }

    TextFieldContainer(
        actionText = actionText,
        enabledConfirm = enabledConfirm,
        onCloseRequest = onCloseRequest,
        onClickConfirm = onClickConfirm,
    ) {
        Tooltip(text = "Input field for \"description\"") {
            OutlinedSingleLineTextField(
                text = "Description",
                value = description,
                onValueChange = { description = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"guest count\"") {
            OutlinedSingleLineTextField(
                text = "Max guest count",
                value = guestCount,
                onValueChange = { guestCount = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Room", style = MaterialTheme.typography.h6)
        Tooltip(text = "Input field for \"room\"") {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenu(
                expanded = expanded,
                items = items.map { it.number.toString() },
                dropdownType = "Room",
                onItemSelected = { item -> room = items.find { it.number.toString() == item } },
                onExpandedChange = { expanded = it },
                shape = MaterialTheme.shapes.medium,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
