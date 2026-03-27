package com.tilegame.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tilegame.Tile
import com.tilegame.theme.QueueSlotDark

@Composable
fun QueueBar(
    queue: List<Tile>,
    maxSize: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(QueueSlotDark.copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until maxSize) {
                val tile = queue.getOrNull(i)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (tile != null) Color.White
                            else Color.White.copy(alpha = 0.1f)
                        )
                        .then(
                            if (tile != null) Modifier.border(
                                1.dp,
                                Color.LightGray.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            ) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (tile != null) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(initialOffsetY = { it }) +
                                    fadeIn() + scaleIn(initialScale = 0.5f)
                        ) {
                            Text(
                                text = tile.emoji,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
