package io.github.tuguzt.ddbms.practice8.view.dialog.create

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.tuguzt.ddbms.practice8.view.dialog.ChoiceButtonRow

@Composable
fun CreateButtonRow(
    onClickCancel: () -> Unit,
    onClickConfirm: () -> Unit,
    enabledConfirm: Boolean,
    modifier: Modifier = Modifier,
) {
    ChoiceButtonRow(
        onClickCancel = onClickCancel,
        cancelTooltip = "Cancel adding document",
        onClickConfirm = onClickConfirm,
        confirmTooltip = "Confirm adding document",
        enabledConfirm = enabledConfirm,
        modifier = modifier,
    )
}
