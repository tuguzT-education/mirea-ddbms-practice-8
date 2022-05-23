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
import androidx.compose.ui.window.rememberDialogState
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
import io.github.tuguzt.ddbms.practice8.view.utils.OneLineText

@Composable
fun NewMockDataDialog(
    onCloseRequest: () -> Unit,
    onCreateData: (MockData) -> Unit,
) {
    Dialog(
        onCloseRequest = onCloseRequest,
        title = "Add new mock data",
        state = rememberDialogState(height = 400.dp),
    ) {
        Content(onCloseRequest = onCloseRequest, onCreateData = onCreateData)
    }
}

@Composable
private fun Content(
    onCloseRequest: () -> Unit,
    onCreateData: (MockData) -> Unit,
) {
    var data1 by remember { mutableStateOf("") }
    var data2 by remember { mutableStateOf("") }
    var data3 by remember { mutableStateOf("") }

    val addAndClose = {
        val user = MockData(data1.toInt(), data2, data3.toLong())

        onCloseRequest()
        onCreateData(user)
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
                value = data1,
                onValueChange = { data1 = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Data 1") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = data2,
                onValueChange = { data2 = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Data 2") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = data3,
                onValueChange = { data3 = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Data 3") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
            ) {
                Button(
                    onClick = addAndClose,
                    enabled = data1.toIntOrNull() != null && data2.isNotBlank() && data3.toLongOrNull() != null,
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
private fun NewMockDataDialogPreview() {
    Practice8Theme {
        Content(onCloseRequest = {}, onCreateData = {})
    }
}
