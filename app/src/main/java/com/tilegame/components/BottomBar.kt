package com.tilegame.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tilegame.theme.BottomBarBg

data class PowerUp(
    val emoji: String,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun BottomBar(
    onShuffle: () -> Unit,
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val powerUps = listOf(
        PowerUp("\uD83D\uDD00", "Shuffle", onShuffle),
        PowerUp("\u21A9\uFE0F", "Undo", onUndo),
        PowerUp("\uD83D\uDCA1", "Hint", onHint),
        PowerUp("\uD83D\uDD17", "Share", onShare),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                BottomBarBg.copy(alpha = 0.9f),
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        powerUps.forEach { powerUp ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { powerUp.onClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF3E7B3E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = powerUp.emoji,
                        fontSize = 24.sp
                    )
                }
                Text(
                    text = powerUp.label,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
