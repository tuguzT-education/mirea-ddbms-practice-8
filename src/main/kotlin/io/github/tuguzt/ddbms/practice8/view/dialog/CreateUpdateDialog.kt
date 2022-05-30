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
        is HotelChain -> HotelChainContent(
            hotelChain = identifiable,
            actionText = actionText,
            onApplyToHotelChain = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        is Hotel -> HotelContent(
            hotel = identifiable,
            actionText = actionText,
            onApplyToHotel = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        is Room -> RoomContent(
            room = identifiable,
            actionText = actionText,
            onApplyToRoom = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        is Booking -> BookingContent(
            booking = identifiable,
            actionText = actionText,
            onApplyToBooking = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        is Client -> ClientContent(
            client = identifiable,
            actionText = actionText,
            onApplyToClient = onApplyToItem,
            onCloseRequest = onCloseRequest,
        )
        else -> when (kClass) {
            // create data by data class of table
            HotelChain::class -> HotelChainContent(
                actionText = actionText,
                onApplyToHotelChain = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            Hotel::class -> HotelContent(
                actionText = actionText,
                onApplyToHotel = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            Room::class -> RoomContent(
                actionText = actionText,
                onApplyToRoom = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            Booking::class -> BookingContent(
                actionText = actionText,
                onApplyToBooking = onApplyToItem,
                onCloseRequest = onCloseRequest,
            )
            Client::class -> ClientContent(
                actionText = actionText,
                onApplyToClient = onApplyToItem,
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
