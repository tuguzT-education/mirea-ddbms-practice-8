package io.github.tuguzt.ddbms.practice8.view.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val surfaceColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
    val textColor = MaterialTheme.colors.surface.copy(alpha = 0.7f)

    TooltipArea(
        tooltip = {
            Surface(
                color = surfaceColor,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.shadow(4.dp),
            ) {
                OneLineText(
                    text = text,
                    color = textColor,
                    modifier = Modifier
                        .background(surfaceColor)
                        .padding(8.dp),
                )
            }
        },
        modifier = modifier,
        delayMillis = 500, // milliseconds
        tooltipPlacement = TooltipPlacement.CursorPoint(alignment = Alignment.BottomEnd),
        content = content,
    )
}
