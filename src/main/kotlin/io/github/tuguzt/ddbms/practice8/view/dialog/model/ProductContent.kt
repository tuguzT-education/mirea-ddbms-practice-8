package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Manufacturer
import io.github.tuguzt.ddbms.practice8.model.Product
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.viewModel
import io.github.tuguzt.ddbms.practice8.view.window.topbar.ExposedDropdownMenu
import io.github.tuguzt.ddbms.practice8.viewmodel.ManufacturerViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToProduct: (Product) -> Unit,
    product: Product? = null,
    manufacturerViewModel: ManufacturerViewModel = viewModel(),
) {
    var name by remember { mutableStateOf(product?.name.orEmpty()) }
    var description by remember { mutableStateOf(product?.description.orEmpty()) }
    var price by remember { mutableStateOf(product?.price?.toString().orEmpty()) }
    var quantity by remember { mutableStateOf(product?.quantity?.toString().orEmpty()) }
    var manufacturer: Manufacturer? by remember { mutableStateOf(null) }

    @Suppress("NAME_SHADOWING")
    val onClickConfirm = {
        if (product == null) {
            onCloseRequest()
            val product = Product(
                name = name,
                description = description,
                price = price.toDouble(),
                quantity = quantity.toULong(),
                manufacturerId = requireNotNull(manufacturer?.id),
                categoryIds = listOf(), // todo
            )
            onApplyToProduct(product)
        } else {
            val product = Product(
                name = name,
                description = description,
                price = price.toDouble(),
                quantity = quantity.toULong(),
                manufacturerId = requireNotNull(manufacturer?.id),
                categoryIds = listOf(), // todo
                id = product.id,
            )
            onApplyToProduct(product)
        }
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (product == null) {
            name.isNotBlank() && description.isNotBlank() && price.toDoubleOrNull() != null
                    && quantity.toULongOrNull() != null && manufacturer != null
        } else run {
            val price = price.toDoubleOrNull() ?: return@run false
            val quantity = quantity.toULongOrNull() ?: return@run false
            val manufacturer = manufacturer ?: return@run false
            name != product.name || description != product.description || price != product.price
                    || quantity != product.quantity || manufacturer.id != product.manufacturerId
        }

    var items: List<Manufacturer> by remember { mutableStateOf(listOf()) }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            items = manufacturerViewModel.getAll()
        }
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

        Tooltip(text = "Input field for \"price\"") {
            OutlinedSingleLineTextField(
                text = "Price",
                value = price,
                onValueChange = { price = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"quantity\"") {
            OutlinedSingleLineTextField(
                text = "Quantity",
                value = quantity,
                onValueChange = { quantity = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Manufacturer", style = MaterialTheme.typography.h6)
        Tooltip(text = "Input field for \"manufacturer\"") {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenu(
                expanded = expanded,
                items = items.map { it.name },
                dropdownType = "Manufacturer",
                onItemSelected = { item -> manufacturer = items.find { it.name == item } },
                onExpandedChange = { expanded = it },
                shape = MaterialTheme.shapes.medium,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // todo product categories
    }
}
