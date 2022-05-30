package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Hotel
import io.github.tuguzt.ddbms.practice8.model.Room
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.viewModel
import io.github.tuguzt.ddbms.practice8.view.window.topbar.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.viewmodel.HotelViewModel
import kotlinx.coroutines.launch

@Composable
fun RoomContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToRoom: (Room) -> Unit,
    room: Room? = null,
    hotelViewModel: HotelViewModel = viewModel(),
) {
    var floor by remember { mutableStateOf(if (room != null) "${room.floor}" else "") }
    var number by remember { mutableStateOf(if (room != null) "${room.number}" else "") }
    var description by remember { mutableStateOf(room?.description ?: "") }
    var guestCountMax by remember { mutableStateOf(if (room != null) "${room.guestCountMax}" else "") }
    var hotel: Hotel? by remember { mutableStateOf(null) }

    var items: List<Hotel> by remember { mutableStateOf(listOf()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            items = hotelViewModel.getAll()
        }
    }

    @Suppress("NAME_SHADOWING")
    val onClickConfirm = {
        if (room == null) {
            onCloseRequest()
            val room = Room(
                floor = floor.toInt(),
                number = number.toInt(),
                description = description,
                guestCountMax = guestCountMax.toInt(),
                hotelId = requireNotNull(hotel?.id),
            )
            onApplyToRoom(room)
        } else {
            val room = Room(
                floor = floor.toInt(),
                number = number.toInt(),
                description = description,
                guestCountMax = guestCountMax.toInt(),
                hotelId = requireNotNull(hotel?.id),
                id = room.id,
            )
            onApplyToRoom(room)
        }
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (room == null) {
            description.isNotBlank() && number.toIntOrNull() != null && floor.toIntOrNull() != null &&
                guestCountMax.toIntOrNull() != null && hotel != null
        } else run {
            val floor = floor.toIntOrNull() ?: return@run false
            val number = number.toIntOrNull() ?: return@run false
            val guestCountMax = guestCountMax.toIntOrNull() ?: return@run false
            val hotel = hotel ?: return@run false
            description != hotel.description || guestCountMax != room.guestCountMax ||
                number != room.number || floor != room.floor || hotel.id != room.hotelId
        }

    TextFieldContainer(
        actionText = actionText,
        enabledConfirm = enabledConfirm,
        onCloseRequest = onCloseRequest,
        onClickConfirm = onClickConfirm,
    ) {
        Tooltip(text = "Input field for \"room number\"") {
            OutlinedSingleLineTextField(
                text = "Room number",
                value = number,
                onValueChange = { number = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"description\"") {
            OutlinedSingleLineTextField(
                text = "Description",
                value = description,
                onValueChange = { description = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"floor\"") {
            OutlinedSingleLineTextField(
                text = "Floor",
                value = floor,
                onValueChange = { floor = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"max guest count\"") {
            OutlinedSingleLineTextField(
                text = "Max guest count",
                value = guestCountMax,
                onValueChange = { guestCountMax = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Hotel", style = MaterialTheme.typography.h6)
        Tooltip(text = "Input field for \"hotel\"") {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenu(
                expanded = expanded,
                items = items.map { it.name },
                dropdownType = "Hotel",
                onItemSelected = { item -> hotel = items.find { it.name == item } },
                onExpandedChange = { expanded = it },
                shape = MaterialTheme.shapes.medium,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
