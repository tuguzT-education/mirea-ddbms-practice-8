package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.HotelChain
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField

@Composable
fun HotelChainContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToHotelChain: (HotelChain) -> Unit,
    hotelChain: HotelChain? = null,
) {
    var name by remember { mutableStateOf(hotelChain?.name.orEmpty()) }
    var description by remember { mutableStateOf(hotelChain?.description.orEmpty()) }

    val onClickConfirm = {
        if (hotelChain == null) {
            onCloseRequest()
            onApplyToHotelChain(HotelChain(name, description))
        } else onApplyToHotelChain(HotelChain(name, description, hotelChain.id))
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (hotelChain == null) name.isNotBlank() && description.isNotBlank()
        else name != hotelChain.name || description != hotelChain.description

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
    }
}
