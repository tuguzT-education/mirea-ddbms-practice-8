package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun ConnectingScreen() {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Connecting…")
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator()
        }
    }
}

@Composable
@Preview
private fun ConnectingScreenPreview() {
    Practice8Theme {
        ConnectingScreen()
    }
}
