package com.tilegame

import java.util.UUID

data class Tile(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val layer: Int,
    val row: Int,
    val col: Int,
    val isRemoved: Boolean = false
)

data class LevelConfig(
    val levelNumber: Int,
    val rows: Int,
    val cols: Int,
    val layers: Int,
    val tileTypes: Int,
    val totalTiles: Int
)

enum class GameState {
    PLAYING,
    WON,
    LOST
}

data class GameUiState(
    val tiles: List<Tile> = emptyList(),
    val queue: List<Tile> = emptyList(),
    val currentLevel: Int = 1,
    val coins: Int = 300,
    val gameState: GameState = GameState.PLAYING,
    val undoStack: List<List<Tile>> = emptyList(),
    val undoQueueStack: List<List<Tile>> = emptyList(),
    val maxQueueSize: Int = 7
)
