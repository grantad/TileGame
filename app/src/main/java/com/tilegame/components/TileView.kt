package com.tilegame.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.tilegame.Tile
import com.tilegame.theme.BlockedOverlay
import com.tilegame.theme.TileWhite

@Composable
fun TileView(
    tile: Tile,
    isBlocked: Boolean,
    isHinted: Boolean,
    tileSize: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val layerOffset = tile.layer * 4
    val shape = RoundedCornerShape(8.dp)

    // Hint animation
    val hintAlpha = if (isHinted) {
        val infiniteTransition = rememberInfiniteTransition(label = "hint")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hintAlpha"
        )
        alpha
    } else {
        1f
    }

    Box(
        modifier = modifier
            .size(tileSize)
            .offset(x = layerOffset.dp, y = (-layerOffset).dp)
            .zIndex(tile.layer.toFloat())
            .shadow(
                elevation = (2 + tile.layer * 2).dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(shape)
            .background(
                if (isHinted) Color(0xFFFFEB3B)
                else TileWhite
            )
            .then(
                if (isBlocked) {
                    Modifier
                        .background(BlockedOverlay)
                        .alpha(0.6f)
                } else {
                    Modifier
                        .alpha(hintAlpha)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onClick() }
                }
            )
            .border(1.dp, Color.LightGray.copy(alpha = 0.3f), shape),
        contentAlignment = Alignment.Center
    ) {
        if (tile.emoji.startsWith("http")) {
            AsyncImage(
                model = tile.emoji,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = tile.emoji,
                fontSize = (tileSize.value * 0.55f).sp
            )
        }
    }
}
