package com.tilegame

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.tilegame.components.BottomBar
import com.tilegame.components.QueueBar
import com.tilegame.components.TileView
import com.tilegame.components.TopBar
import com.tilegame.theme.SkyBlueDark
import com.tilegame.theme.SkyBlueLight

@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val hintTileId by viewModel.hintTileId.collectAsState()
    val showSplash by viewModel.showSplashScreen.collectAsState()
    val view = LocalView.current

    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            SkyBlueDark,
            SkyBlueLight,
            Color(0xFFB8E6F0)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar
            TopBar(
                level = uiState.currentLevel,
                coins = uiState.coins
            )

            // Queue bar
            QueueBar(
                queue = uiState.queue,
                maxSize = uiState.maxQueueSize
            )

            // Game board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GameBoard(
                    tiles = uiState.tiles,
                    hintTileId = hintTileId,
                    onTileTap = { tile ->
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        viewModel.onTileTap(tile)
                    }
                )
            }

            // Bottom bar
            BottomBar(
                onShuffle = { viewModel.onShuffle() },
                onUndo = { viewModel.onUndo() },
                onHint = { viewModel.onHint() },
                onShare = { }
            )
        }

        // Splash Screen
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize().zIndex(10f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF010B1C)), // Dark blue background to match image
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://i.ibb.co/C0mXN3K/palmettodevs-splash.png",
                    contentDescription = "Splash Screen",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit // Fit ensures the logo and text are not cut off
                )
            }
        }

        // Win / Game Over overlay
        AnimatedVisibility(
            visible = uiState.gameState != GameState.PLAYING && !showSplash,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.fillMaxSize().zIndex(5f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(32.dp)
                        .width(260.dp)
                ) {
                    Text(
                        text = if (uiState.gameState == GameState.WON) "\uD83C\uDF89" else "\uD83D\uDE22",
                        fontSize = 64.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (uiState.gameState == GameState.WON) "Level Complete!" else "Game Over",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.gameState == GameState.WON) Color(0xFF4CAF50) else Color(0xFFE53935),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (uiState.gameState == GameState.WON)
                            "Great job! Ready for the next challenge?"
                        else
                            "The queue is full! Try again?",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (uiState.gameState == GameState.WON) {
                                viewModel.nextLevel()
                            } else {
                                viewModel.restartLevel()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.gameState == GameState.WON)
                                Color(0xFF4CAF50)
                            else Color(0xFFE53935)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (uiState.gameState == GameState.WON) "Next Level" else "Try Again",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameBoard(
    tiles: List<Tile>,
    hintTileId: String?,
    onTileTap: (Tile) -> Unit
) {
    val activeTiles = tiles.filter { !it.isRemoved }
    val blockedSet = remember(activeTiles) {
        activeTiles.filter { GameLogic.isTileBlocked(it, activeTiles) }.map { it.id }.toSet()
    }

    if (activeTiles.isEmpty()) return

    val maxRow = activeTiles.maxOf { it.row }
    val maxCol = activeTiles.maxOf { it.col }
    val maxLayer = activeTiles.maxOf { it.layer }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth - 16.dp
        val tileSize = minOf(
            (availableWidth / (maxCol + 2)).value,
            48f
        ).dp
        val layerPadding = maxLayer * 4

        val boardWidth = (maxCol + 1) * tileSize.value + layerPadding
        val boardHeight = (maxRow + 1) * tileSize.value + layerPadding

        Box(
            modifier = Modifier
                .width(boardWidth.dp)
                .height(boardHeight.dp)
        ) {
            // Render tiles from bottom layer to top
            val sortedTiles = activeTiles.sortedBy { it.layer }
            sortedTiles.forEach { tile ->
                key(tile.id) {
                    val x = tile.col * tileSize.value
                    val y = tile.row * tileSize.value

                    TileView(
                        tile = tile,
                        isBlocked = tile.id in blockedSet,
                        isHinted = tile.id == hintTileId,
                        tileSize = tileSize,
                        onClick = { onTileTap(tile) },
                        modifier = Modifier.offset(x = x.dp, y = y.dp)
                    )
                }
            }
        }
    }
}
