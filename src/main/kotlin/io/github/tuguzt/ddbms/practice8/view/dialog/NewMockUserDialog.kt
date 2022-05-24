package io.github.tuguzt.ddbms.practice8.view.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
import io.github.tuguzt.ddbms.practice8.view.utils.ChoiceButtonRow
import io.github.tuguzt.ddbms.practice8.view.utils.SingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.utils.Tooltip

@Composable
fun NewMockUserDialog(
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

    val focusManager = LocalFocusManager.current
    Surface(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = focusManager::clearFocus,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Tooltip(text = "Input field for \"name\"") {
                SingleLineTextField(
                    text = "Name",
                    value = name,
                    onValueChange = { name = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Tooltip(text = "Input field for \"age\"") {
                SingleLineTextField(
                    text = "Age",
                    value = age,
                    onValueChange = { age = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            ChoiceButtonRow(
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
private fun NewMockUserDialogPreview() {
    Practice8Theme {
        Content(onCloseRequest = {}, onCreateUser = {})
    }
}
