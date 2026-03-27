package com.tilegame

import kotlin.random.Random

object GameLogic {

    val allEmojis = listOf(
        "\uD83D\uDC36", // dog
        "\uD83D\uDC31", // cat
        "\uD83D\uDC3B", // bear
        "\uD83D\uDC37", // pig
        "\uD83D\uDC38", // frog
        "\uD83D\uDC35", // monkey
        "\uD83D\uDC25", // chick
        "https://gamecocktraditions.com/cdn/shop/files/CADEC00543_5000x.jpg?v=1721750734", // replaced fox
        "\uD83E\uDD81", // lion
        "\uD83D\uDC2F", // tiger
        "\uD83C\uDF4E", // apple
        "\uD83C\uDF4A", // orange
        "\uD83C\uDF53", // strawberry
        "\uD83C\uDF52", // cherries
        "\uD83C\uDF47", // grapes
        "\uD83C\uDF49", // watermelon
        "\uD83C\uDF51", // peach
        "\uD83C\uDF4B", // lemon
        "\u2B50",       // star
        "\uD83C\uDF19", // moon
        "\u2764\uFE0F", // heart
        "\uD83D\uDD25", // fire
        "\uD83C\uDF3A", // hibiscus
        "\uD83C\uDF3B", // sunflower
    )

    fun getLevelConfig(level: Int): LevelConfig {
        return when (level) {
            1 -> LevelConfig(
                levelNumber = 1,
                rows = 5,
                cols = 6,
                layers = 2,
                tileTypes = 6,
                totalTiles = 36
            )
            2 -> LevelConfig(
                levelNumber = 2,
                rows = 6,
                cols = 7,
                layers = 3,
                tileTypes = 8,
                totalTiles = 54
            )
            3 -> LevelConfig(
                levelNumber = 3,
                rows = 7,
                cols = 8,
                layers = 3,
                tileTypes = 10,
                totalTiles = 72
            )
            else -> LevelConfig(
                levelNumber = level,
                rows = 7,
                cols = 8,
                layers = 3,
                tileTypes = minOf(12, 6 + level),
                totalTiles = 72
            )
        }
    }

    fun generateLevel(config: LevelConfig): List<Tile> {
        val emojis = allEmojis.shuffled().take(config.tileTypes)
        // Total tiles must be divisible by 3
        val adjustedTotal = (config.totalTiles / 3) * 3

        // Create tiles in groups of 3
        val tileEmojis = mutableListOf<String>()
        val groupCount = adjustedTotal / 3
        for (i in 0 until groupCount) {
            val emoji = emojis[i % emojis.size]
            repeat(3) { tileEmojis.add(emoji) }
        }
        tileEmojis.shuffle()

        val tiles = mutableListOf<Tile>()
        var emojiIndex = 0

        for (layer in 0 until config.layers) {
            // Each higher layer is slightly smaller / offset
            val layerRows = config.rows - layer
            val layerCols = config.cols - layer
            val startRow = layer / 2
            val startCol = layer / 2

            // Determine how many tiles to place on this layer
            val tilesPerLayer = if (layer < config.layers - 1) {
                // Distribute roughly evenly, but ensure divisible by 3
                val raw = adjustedTotal / config.layers
                (raw / 3) * 3
            } else {
                // Remaining tiles
                adjustedTotal - emojiIndex
            }

            // Generate random positions on this layer
            val positions = mutableListOf<Pair<Int, Int>>()
            for (r in 0 until layerRows) {
                for (c in 0 until layerCols) {
                    positions.add(Pair(startRow + r, startCol + c))
                }
            }
            positions.shuffle()

            val placementCount = minOf(tilesPerLayer, positions.size, adjustedTotal - emojiIndex)
            for (i in 0 until placementCount) {
                if (emojiIndex >= tileEmojis.size) break
                val (row, col) = positions[i]
                tiles.add(
                    Tile(
                        emoji = tileEmojis[emojiIndex],
                        layer = layer,
                        row = row,
                        col = col
                    )
                )
                emojiIndex++
            }
        }

        return tiles
    }

    fun isTileBlocked(tile: Tile, allTiles: List<Tile>): Boolean {
        if (tile.isRemoved) return true
        // A tile is blocked if any non-removed tile on a higher layer overlaps it
        return allTiles.any { other ->
            !other.isRemoved &&
            other.layer > tile.layer &&
            other.row >= tile.row - 1 && other.row <= tile.row + 1 &&
            other.col >= tile.col - 1 && other.col <= tile.col + 1
        }
    }

    fun getUnblockedTiles(tiles: List<Tile>): List<Tile> {
        val activeTiles = tiles.filter { !it.isRemoved }
        return activeTiles.filter { !isTileBlocked(it, activeTiles) }
    }

    fun addTileToQueue(queue: List<Tile>, tile: Tile): List<Tile> {
        val newQueue = queue.toMutableList()
        // Find where to insert (next to matching tile if exists)
        val matchIndex = newQueue.indexOfLast { it.emoji == tile.emoji }
        if (matchIndex >= 0) {
            newQueue.add(matchIndex + 1, tile)
        } else {
            newQueue.add(tile)
        }
        return newQueue
    }

    fun clearMatchesFromQueue(queue: List<Tile>): Pair<List<Tile>, List<String>> {
        val result = queue.toMutableList()
        val clearedIds = mutableListOf<String>()

        // Count emojis
        val emojiCounts = result.groupBy { it.emoji }
        for ((emoji, group) in emojiCounts) {
            if (group.size >= 3) {
                // Remove first 3 matching
                var removed = 0
                val toRemove = group.take(3).map { it.id }.toSet()
                clearedIds.addAll(toRemove)
                result.removeAll { it.id in toRemove }
            }
        }

        return Pair(result, clearedIds)
    }

    fun checkGameOver(queue: List<Tile>, maxSize: Int): Boolean {
        return queue.size >= maxSize
    }

    fun checkWin(tiles: List<Tile>): Boolean {
        return tiles.all { it.isRemoved }
    }

    fun shuffleTiles(tiles: List<Tile>): List<Tile> {
        val activeTiles = tiles.filter { !it.isRemoved }
        val removedTiles = tiles.filter { it.isRemoved }

        val emojis = activeTiles.map { it.emoji }.shuffled()
        val shuffled = activeTiles.mapIndexed { index, tile ->
            tile.copy(emoji = emojis[index])
        }

        return shuffled + removedTiles
    }

    fun findHint(tiles: List<Tile>, queue: List<Tile>): Tile? {
        val unblocked = getUnblockedTiles(tiles)
        // Find a tile whose emoji already has 2 in queue
        val queueCounts = queue.groupBy { it.emoji }.mapValues { it.value.size }
        for ((emoji, count) in queueCounts) {
            if (count >= 2) {
                val hint = unblocked.firstOrNull { it.emoji == emoji }
                if (hint != null) return hint
            }
        }
        // Find a tile whose emoji has 1 in queue
        for ((emoji, count) in queueCounts) {
            if (count >= 1) {
                val hint = unblocked.firstOrNull { it.emoji == emoji }
                if (hint != null) return hint
            }
        }
        // Just return any unblocked tile
        return unblocked.firstOrNull()
    }
}
