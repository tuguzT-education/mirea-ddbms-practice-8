package io.github.tuguzt.ddbms.practice8.view.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
import io.github.tuguzt.ddbms.practice8.view.utils.OneLineText

@Composable
fun NewMockUserDialog(
    onCloseRequest: () -> Unit,
    onCreateUser: (MockUser) -> Unit,
) {
    Dialog(onCloseRequest = onCloseRequest, title = "Add new mock user") {
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
        val user = MockUser(name, age = age.toInt())

        onCloseRequest()
        onCreateUser(user)
    }

    val focusManager = LocalFocusManager.current
    Surface(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = focusManager::clearFocus,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Name") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Age") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
            ) {
                Button(
                    onClick = addAndClose,
                    enabled = name.isNotBlank() && age.toIntOrNull() != null,
                    content = { OneLineText("Add") },
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onCloseRequest,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                    content = { OneLineText(text = "Cancel") },
                )
            }
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
