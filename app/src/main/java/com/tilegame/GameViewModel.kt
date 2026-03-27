package com.tilegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var _hintTileId: MutableStateFlow<String?> = MutableStateFlow(null)
    val hintTileId: StateFlow<String?> = _hintTileId.asStateFlow()

    private val _showSplashScreen = MutableStateFlow(true)
    val showSplashScreen: StateFlow<Boolean> = _showSplashScreen.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000) // Show splash for 2 seconds
            _showSplashScreen.value = false
        }
        startLevel(1)
    }

    fun startLevel(level: Int) {
        val config = GameLogic.getLevelConfig(level)
        val tiles = GameLogic.generateLevel(config)
        _uiState.value = GameUiState(
            tiles = tiles,
            queue = emptyList(),
            currentLevel = level,
            coins = _uiState.value.coins,
            gameState = GameState.PLAYING,
            undoStack = emptyList(),
            undoQueueStack = emptyList()
        )
        _hintTileId.value = null
    }

    fun onTileTap(tile: Tile) {
        val state = _uiState.value
        if (state.gameState != GameState.PLAYING) return
        if (tile.isRemoved) return
        if (GameLogic.isTileBlocked(tile, state.tiles.filter { !it.isRemoved })) return
        if (state.queue.size >= state.maxQueueSize) return

        _hintTileId.value = null

        // Save undo state
        val newUndoStack = state.undoStack + listOf(state.tiles)
        val newUndoQueueStack = state.undoQueueStack + listOf(state.queue)

        // Remove tile from board
        val newTiles = state.tiles.map {
            if (it.id == tile.id) it.copy(isRemoved = true) else it
        }

        // Add to queue
        val newQueue = GameLogic.addTileToQueue(state.queue, tile)

        // Check for matches
        val (clearedQueue, clearedIds) = GameLogic.clearMatchesFromQueue(newQueue)

        // Check win
        val hasWon = GameLogic.checkWin(newTiles) && clearedQueue.isEmpty()

        // Check game over
        val isGameOver = GameLogic.checkGameOver(clearedQueue, state.maxQueueSize)

        _uiState.update {
            it.copy(
                tiles = newTiles,
                queue = clearedQueue,
                gameState = when {
                    hasWon -> GameState.WON
                    isGameOver -> GameState.LOST
                    else -> GameState.PLAYING
                },
                undoStack = newUndoStack,
                undoQueueStack = newUndoQueueStack
            )
        }
    }

    fun onShuffle() {
        val state = _uiState.value
        if (state.gameState != GameState.PLAYING) return
        if (state.coins < 50) return

        _hintTileId.value = null
        val shuffled = GameLogic.shuffleTiles(state.tiles)
        _uiState.update {
            it.copy(
                tiles = shuffled,
                coins = it.coins - 50
            )
        }
    }

    fun onUndo() {
        val state = _uiState.value
        if (state.gameState != GameState.PLAYING) return
        if (state.undoStack.isEmpty()) return

        _hintTileId.value = null
        val previousTiles = state.undoStack.last()
        val previousQueue = state.undoQueueStack.last()

        _uiState.update {
            it.copy(
                tiles = previousTiles,
                queue = previousQueue,
                undoStack = it.undoStack.dropLast(1),
                undoQueueStack = it.undoQueueStack.dropLast(1)
            )
        }
    }

    fun onHint() {
        val state = _uiState.value
        if (state.gameState != GameState.PLAYING) return

        val hint = GameLogic.findHint(state.tiles, state.queue)
        _hintTileId.value = hint?.id
    }

    fun nextLevel() {
        startLevel(_uiState.value.currentLevel + 1)
    }

    fun restartLevel() {
        startLevel(_uiState.value.currentLevel)
    }
}
