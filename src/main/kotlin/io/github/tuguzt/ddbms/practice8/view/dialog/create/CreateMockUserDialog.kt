package io.github.tuguzt.ddbms.practice8.view.dialog.create

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
fun CreateMockUserDialog(
    onCloseRequest: () -> Unit,
    onCreateUser: (MockUser) -> Unit,
) {
    Dialog(
        title = "Add new mock user",
        onCloseRequest = onCloseRequest,
    ) {
        Content(onCloseRequest = onCloseRequest, onCreateUser = onCreateUser)
    }
}

@Composable
private fun Content(
    onCloseRequest: () -> Unit,
    onCreateUser: (MockUser) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    val addAndClose = {
        val user = MockUser(name, age.toInt())

        onCloseRequest()
        onCreateUser(user)
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

            CreateButtonRow(
                onClickCancel = onCloseRequest,
                onClickConfirm = addAndClose,
                enabledConfirm = name.isNotBlank() && age.toIntOrNull() != null,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
@Preview
private fun CreateMockUserDialogPreview() {
    Practice8Theme {
        Content(onCloseRequest = {}, onCreateUser = {})
    }
}
