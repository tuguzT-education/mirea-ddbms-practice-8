package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Identifiable
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField

@Composable
fun MockUserContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToUser: (Identifiable<*>) -> Unit,
    user: MockUser? = null,
) {
    var name by remember { mutableStateOf(user?.name ?: "") }
    var age by remember { mutableStateOf(if (user != null) "${user.age}" else "") }

    val onClickConfirm = {
        if (user == null) {
            onCloseRequest()
            onApplyToUser(MockUser(name, age.toInt()))
        } else onApplyToUser(MockUser(name, age.toInt(), user.id))
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (user == null)
            name.isNotBlank() && age.toIntOrNull() != null
        else run {
            val age = age.toIntOrNull() ?: return@run false
            name != user.name || age != user.age
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

        Tooltip(text = "Input field for \"age\"") {
            OutlinedSingleLineTextField(
                text = "Age",
                value = age,
                onValueChange = { age = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
