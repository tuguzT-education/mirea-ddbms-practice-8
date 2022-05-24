package io.github.tuguzt.ddbms.practice8.view.window.table

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.OneLineText

@Composable
private fun TableBanner(
    headerText: String,
    resourcePath: String,
    contentDescription: String,
    content: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Icon(
            painter = painterResource(resourcePath),
            contentDescription = contentDescription,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))

        OneLineText(
            text = headerText,
            fontSize = MaterialTheme.typography.h6.fontSize,
        )
        Spacer(modifier = Modifier.height(8.dp))

        content()
    }
}

@Composable
fun EmptyTableBanner() = TableBanner(
    headerText = "Table is empty",
    resourcePath = "icons/document.svg",
    contentDescription = "There is no data in table",
) {
    Text(
        maxLines = 2,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        text = "There is no data in this table yet." +
                "\nYou can add new document at any time!"
    )
}

@Composable
fun LoadingTableBanner() = TableBanner(
    headerText = "Data is loading",
    resourcePath = "icons/sync.svg",
    contentDescription = "Wait until data is loaded",
) {
    OneLineText(
        text = "Please wait until all data is loaded.",
        textAlign = TextAlign.Center,
    )
}
