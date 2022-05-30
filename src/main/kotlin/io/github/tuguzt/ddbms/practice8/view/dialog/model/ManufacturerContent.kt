package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Manufacturer
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField

@Composable
fun ManufacturerContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToManufacturer: (Manufacturer) -> Unit,
    manufacturer: Manufacturer? = null,
) {
    var name by remember { mutableStateOf(manufacturer?.name.orEmpty()) }
    var description by remember { mutableStateOf(manufacturer?.description.orEmpty()) }

    val onClickConfirm = {
        if (manufacturer == null) {
            onCloseRequest()
            onApplyToManufacturer(Manufacturer(name, description))
        } else onApplyToManufacturer(Manufacturer(name, description, manufacturer.id))
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (manufacturer == null) name.isNotBlank() && description.isNotBlank()
        else name != manufacturer.name || description != manufacturer.description

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
