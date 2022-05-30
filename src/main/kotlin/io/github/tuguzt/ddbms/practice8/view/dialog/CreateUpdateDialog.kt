package io.github.tuguzt.ddbms.practice8.view.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import io.github.tuguzt.ddbms.practice8.capitalize
import io.github.tuguzt.ddbms.practice8.model.*
import io.github.tuguzt.ddbms.practice8.view.dialog.model.*
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

@Composable
fun CreateDialog(
    onCloseRequest: () -> Unit,
    onApplyToItem: (Identifiable<*>) -> Unit,
    kClass: KClass<out Identifiable<*>>,
) = BaseDialog(
    actionText = "creat",
    onApplyToItem = onApplyToItem,
    onCloseRequest = onCloseRequest,
    kClass = kClass,
)

@Composable
fun UpdateDialog(
    identifiable: Identifiable<*>,
    onApplyToItem: (Identifiable<*>) -> Unit,
    onCloseRequest: () -> Unit,
) = BaseDialog(
    actionText = "updat",
    identifiable = identifiable,
    onApplyToItem = onApplyToItem,
    onCloseRequest = onCloseRequest,
)

@Composable
private fun BaseDialog(
    actionText: String,
    onApplyToItem: (Identifiable<*>) -> Unit,
    onCloseRequest: () -> Unit,
    identifiable: Identifiable<*>? = null,
    kClass: KClass<out Identifiable<*>>? = null,
) {
    val fieldNumber =
        if (identifiable != null) identifiable::class.memberProperties.count()
        else requireNotNull(kClass).memberProperties.count()

    Dialog(
        title = "${actionText.capitalize()}e " +
                if (identifiable != null) identifiable::class.simpleName
                else requireNotNull(kClass).simpleName,
        onCloseRequest = onCloseRequest,
        state = rememberDialogState(height = (fieldNumber * 100).dp),
    ) {
        Content(
            actionText = actionText,
            identifiable = identifiable,
            onApplyToItem = onApplyToItem,
            onCloseRequest = onCloseRequest,
            kClass = kClass,
        )
    }
}

// todo "add clause for each table"
@Composable
private fun Content(
    actionText: String,
    onApplyToItem: (Identifiable<*>) -> Unit,
    onCloseRequest: () -> Unit,
    identifiable: Identifiable<*>? = null,
    kClass: KClass<out Identifiable<*>>? = null,
) {
    when (identifiable) {
        // update data by data class of table
        is User -> UserContent(
            user = identifiable,
            actionText = actionText,
            onApplyToUser = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        is Manufacturer -> ManufacturerContent(
            manufacturer = identifiable,
            actionText = actionText,
            onApplyToManufacturer = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        is Order -> OrderContent(
            order = identifiable,
            actionText = actionText,
            onApplyToOrder = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        is Product -> ProductContent(
            product = identifiable,
            actionText = actionText,
            onApplyToProduct = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        is ProductCategory -> ProductCategoryContent(
            category = identifiable,
            actionText = actionText,
            onApplyToCategory = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        else -> when (kClass) {
            // create data by data class of table
            User::class -> UserContent(
                actionText = actionText,
                onApplyToUser = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            Manufacturer::class -> ManufacturerContent(
                actionText = actionText,
                onApplyToManufacturer = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            Order::class -> OrderContent(
                actionText = actionText,
                onApplyToOrder = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            Product::class -> ProductContent(
                actionText = actionText,
                onApplyToProduct = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            ProductCategory::class -> ProductCategoryContent(
                actionText = actionText,
                onApplyToCategory = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            else -> throw IllegalArgumentException(
                "Some Identifiable's weren't specified"
            )
        }
    }
}

@Composable
@Preview
private fun UpdateDialogPreview() {
    Practice8Theme {
        Content(
            actionText = "updat",
            onApplyToItem = {},
            onCloseRequest = {},
            kClass = User::class,
        )
    }
}
