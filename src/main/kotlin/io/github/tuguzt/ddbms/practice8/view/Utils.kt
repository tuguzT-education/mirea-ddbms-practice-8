package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun OneLineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        textAlign = textAlign,
        fontWeight = fontWeight,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

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
        content = content,
        modifier = modifier,
        delayMillis = 500, // milliseconds
        tooltipPlacement = TooltipPlacement.CursorPoint(alignment = Alignment.BottomEnd),
    )
}
