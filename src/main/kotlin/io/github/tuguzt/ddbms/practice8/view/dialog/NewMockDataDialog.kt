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
import io.github.tuguzt.ddbms.practice8.view.utils.ChoiceButtonRow
import io.github.tuguzt.ddbms.practice8.view.utils.SingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.utils.Tooltip

@Composable
fun NewMockDataDialog(
    onCloseRequest: () -> Unit,
    onCreateData: (MockData) -> Unit,
) {
    Dialog(
        title = "Add new mock data",
        state = rememberDialogState(height = 400.dp),
        onCloseRequest = onCloseRequest,
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
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = focusManager::clearFocus,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Tooltip(text = "Input field for \"data 1\"") {
                SingleLineTextField(
                    text = "Data 1",
                    value = data1,
                    onValueChange = { data1 = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Tooltip(text = "Input field for \"data 2\"") {
                SingleLineTextField(
                    text = "Data 2",
                    value = data2,
                    onValueChange = { data2 = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Tooltip(text = "Input field for \"data 3\"") {
                SingleLineTextField(
                    text = "Data 3",
                    value = data3,
                    onValueChange = { data3 = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            ChoiceButtonRow(
                onClickCancel = onCloseRequest,
                onClickConfirm = addAndClose,
                enabledConfirm = data1.toIntOrNull() != null
                        && data2.isNotBlank()
                        && data3.toLongOrNull() != null,
                modifier = Modifier.weight(1f),
            )
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
