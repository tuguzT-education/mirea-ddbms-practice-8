package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.User
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField

@Composable
fun UserContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToUser: (User) -> Unit,
    user: User? = null,
) {
    var name by remember { mutableStateOf(user?.name.orEmpty()) }
    var surname by remember { mutableStateOf(user?.surname.orEmpty()) }
    var email by remember { mutableStateOf(user?.email.orEmpty()) }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber?.toString().orEmpty()) }

    val onClickConfirm = {
        if (user == null) {
            onCloseRequest()
            onApplyToUser(User(name, surname, email, phoneNumber.toULong()))
        } else onApplyToUser(User(name, surname, email, phoneNumber.toULong(), user.id))
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (user == null) {
            (name.isNotBlank() && surname.isNotBlank() && email.isNotBlank()
                    && phoneNumber.toULongOrNull() != null)
        } else run {
            val phoneNumber = phoneNumber.toULongOrNull() ?: return@run false
            name != user.name || surname != user.surname || email != user.email
                    || phoneNumber != user.phoneNumber
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

        Tooltip(text = "Input field for \"email\"") {
            OutlinedSingleLineTextField(
                text = "Email",
                value = email,
                onValueChange = { email = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Tooltip(text = "Input field for \"phone number\"") {
            OutlinedSingleLineTextField(
                text = "Phone number",
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
