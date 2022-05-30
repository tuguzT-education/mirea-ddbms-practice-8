package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.ProductCategory
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField

@Composable
fun ProductCategoryContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToCategory: (ProductCategory) -> Unit,
    category: ProductCategory? = null,
) {
    var name by remember { mutableStateOf(category?.name.orEmpty()) }
    var description by remember { mutableStateOf(category?.description.orEmpty()) }

    val onClickConfirm = {
        if (category == null) {
            onCloseRequest()
            onApplyToCategory(ProductCategory(name, description))
        } else onApplyToCategory(ProductCategory(name, description, category.id))
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (category == null) name.isNotBlank() && description.isNotBlank()
        else name != category.name || description != category.description

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
