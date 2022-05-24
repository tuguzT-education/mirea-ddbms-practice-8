package io.github.tuguzt.ddbms.practice8.view.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.OneLineText
import io.github.tuguzt.ddbms.practice8.view.Tooltip

@Composable
fun OutlinedSingleLineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
) {
    OutlinedTextField(
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        label = { OneLineText(text = text) },
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun IconTextButton(
    onClick: () -> Unit,
    text: String,
    resourcePath: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
    Button(
        colors = colors,
        enabled = enabled,
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom = 12.dp
        ),
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(resourcePath),
                contentDescription = contentDescription,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            OneLineText(text = text)
        }
    }
}

@Composable
fun ChoiceButtonRow(
    onClickCancel: () -> Unit,
    onClickConfirm: () -> Unit,
    enabledConfirm: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = modifier.fillMaxWidth(),
    ) {
        Tooltip(text = "Confirm adding document ...") {
            IconTextButton(
                text = "Confirm",
                onClick = onClickConfirm,
                enabled = enabledConfirm,
                resourcePath = "icons/ok.svg",
                contentDescription = "Confirm adding document ...",
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Tooltip(text = "Cancel adding document ...") {
            IconTextButton(
                text = "Cancel",
                onClick = onClickCancel,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.error
                ),
                resourcePath = "icons/cancel.svg",
                contentDescription = "Cancel adding document ...",
            )
        }
    }
}
