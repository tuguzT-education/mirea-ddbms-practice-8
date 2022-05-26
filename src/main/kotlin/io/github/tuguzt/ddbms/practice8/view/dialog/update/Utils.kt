package io.github.tuguzt.ddbms.practice8.view.dialog.update

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.tuguzt.ddbms.practice8.view.dialog.ChoiceButtonRow

@Composable
fun UpdateButtonRow(
    onClickCancel: () -> Unit,
    onClickConfirm: () -> Unit,
    enabledConfirm: Boolean,
    modifier: Modifier = Modifier,
) {
    ChoiceButtonRow(
        onClickCancel = onClickCancel,
        cancelTooltip = "Cancel updating document",
        onClickConfirm = onClickConfirm,
        confirmTooltip = "Confirm updating document",
        enabledConfirm = enabledConfirm,
        modifier = modifier,
    )
}
