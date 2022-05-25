package io.github.tuguzt.ddbms.practice8.view.dialog.update

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.DialogSurface
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun UpdateMockUserDialog(
    onCloseRequest: () -> Unit,
    user: MockUser,
    onUpdateUser: (MockUser) -> Unit,
) {
    Dialog(
        title = "Update mock user",
        onCloseRequest = onCloseRequest,
    ) {
        Content(onCloseRequest = onCloseRequest, user = user, onUpdateUser = onUpdateUser)
    }
}

@Composable
private fun Content(
    onCloseRequest: () -> Unit,
    user: MockUser,
    onUpdateUser: (MockUser) -> Unit,
) {
    var name by remember { mutableStateOf(user.name) }
    var age by remember { mutableStateOf("${user.age}") }

    @Suppress("NAME_SHADOWING")
    val update = {
        val user = MockUser(name, age.toInt())
        onUpdateUser(user)
    }

    DialogSurface {
        Column(modifier = Modifier.matchParentSize()) {
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

            UpdateButtonRow(
                onClickCancel = onCloseRequest,
                onClickConfirm = update,
                enabledConfirm = name != user.name && age.toIntOrNull() != user.age,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
@Preview
private fun UpdateMockUserDialogPreview() {
    Practice8Theme {
        Content(
            onCloseRequest = {},
            user = MockUser(name = "eri", age = 54347),
            onUpdateUser = {},
        )
    }
}
