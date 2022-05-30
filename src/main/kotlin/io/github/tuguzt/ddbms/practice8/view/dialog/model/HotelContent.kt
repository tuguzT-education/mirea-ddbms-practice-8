package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Hotel
import io.github.tuguzt.ddbms.practice8.model.HotelChain
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.viewModel
import io.github.tuguzt.ddbms.practice8.view.window.topbar.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.viewmodel.HotelChainViewModel
import kotlinx.coroutines.launch

@Composable
fun HotelContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToHotel: (Hotel) -> Unit,
    hotel: Hotel? = null,
    hotelChainViewModel: HotelChainViewModel = viewModel(),
) {
    var name by remember { mutableStateOf(hotel?.name ?: "") }
    var floorMax by remember { mutableStateOf(if (hotel != null) "${hotel.floorMax}" else "") }
    var description by remember { mutableStateOf(hotel?.description ?: "") }
    var hotelChain: HotelChain? by remember { mutableStateOf(null) }

    var items: List<HotelChain> by remember { mutableStateOf(listOf()) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            items = hotelChainViewModel.getAll()
        }
    }

    @Suppress("NAME_SHADOWING")
    val onClickConfirm = {
        if (hotel == null) {
            onCloseRequest()
            val hotel = Hotel(
                name = name,
                floorMax = floorMax.toInt(),
                description = description,
                hotelChainId = requireNotNull(hotelChain?.id),
            )
            onApplyToHotel(hotel)
        } else {
            val hotel = Hotel(
                name = name,
                floorMax = floorMax.toInt(),
                description = description,
                hotelChainId = requireNotNull(hotelChain?.id),
                id = hotel.id,
            )
            onApplyToHotel(hotel)
        }
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (hotel == null) {
            name.isNotBlank() && description.isNotBlank() &&
                floorMax.toIntOrNull() != null && hotelChain != null
        } else run {
            val floorMax = floorMax.toIntOrNull() ?: return@run false
            val hotelChain = hotelChain ?: return@run false
            name != hotel.name || description != hotel.description ||
                floorMax != hotel.floorMax || hotelChain.id != hotel.hotelChainId
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

        Tooltip(text = "Input field for \"description\"") {
            OutlinedSingleLineTextField(
                text = "Description",
                value = description,
                onValueChange = { description = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"max floors\"") {
            OutlinedSingleLineTextField(
                text = "Max floors",
                value = floorMax,
                onValueChange = { floorMax = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Hotel chain", style = MaterialTheme.typography.h6)
        Tooltip(text = "Input field for \"hotel chain\"") {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenu(
                expanded = expanded,
                items = items.map { it.name },
                dropdownType = "Hotel chain",
                onItemSelected = { item -> hotelChain = items.find { it.name == item } },
                onExpandedChange = { expanded = it },
                shape = MaterialTheme.shapes.medium,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
