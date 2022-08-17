import kotlin.random.Random

private const val TREE_COUNT = 4

typealias pickFruitStrategy = (KarasuBoard) -> Int

class KarasuBoard(
    initialFruitCount: Int = 10,
    initialKarasuPieces: Int = 9,
) {
    val fruitsInTree: Array<Int> = Array(TREE_COUNT) { initialFruitCount }
    var karasuPieces = initialKarasuPieces

    fun play(pickFruitsStrategy: pickFruitStrategy): Boolean {
        while (!isGameFinished) {
            step(pickFruitsStrategy)
        }
        return playerWins
    }

    private fun step(pickFruit: pickFruitStrategy) {
        when (val dice = Random.nextInt(6)) {
            0, 1, 2, 3 -> {
                removeFruit(dice)
            }

            4 -> {
                removeFruit(pickFruit(this))
                removeFruit(pickFruit(this))
            }

            5 -> {
                --karasuPieces
            }
        }
    }

    val isGameFinished get() = playerWins || karasuWins

    val playerWins get() = fruitsInTree.all { it == 0 }

    val karasuWins get() = karasuPieces == 0

    private fun removeFruit(fruitToRemove: Int) {
        if (fruitToRemove in 0..3 && fruitsInTree[fruitToRemove] > 0)
            fruitsInTree[fruitToRemove]--
    }
}

fun KarasuBoard.println() {
    println("F: ${fruitsInTree.joinToString(",")} K: $karasuPieces")
}

val KarasuBoard.treesWithFruits
    get() = fruitsInTree.indices.asSequence()
        .filter { fruitsInTree[it] > 0 }
        .ifEmpty { sequenceOf(0) }

val randomFruitStrategy: pickFruitStrategy = { board ->
    board.treesWithFruits.shuffled().first()
}

val minFruitStrategy: pickFruitStrategy = { board ->
    board.treesWithFruits.minBy { tree -> board.fruitsInTree[tree] }
}

val maxFruitStrategy: pickFruitStrategy = { board ->
    board.treesWithFruits.maxBy { tree -> board.fruitsInTree[tree] }
}

val firstFruitStrategy: pickFruitStrategy = { board ->
    board.treesWithFruits.first()
}

val zeroFruitsStrategy: pickFruitStrategy = { board -> 0 }

val strategies = listOf(
    Pair("random", randomFruitStrategy),
    Pair("min", minFruitStrategy),
    Pair("max", maxFruitStrategy),
    Pair("first", firstFruitStrategy),
    Pair("zero", zeroFruitsStrategy),
)

const val GAME_COUNT = 100000

fun main() {
    println("karasuPieces fruitPieces strategy playerWins/count %playerWins")

    strategies.forEach { (strategyName, strategy) ->
        (1..20).forEach { karasuPieces ->
            (1..20).forEach { fruitPieces ->
                var playerWinCount = 0
                var count = 0

                repeat(GAME_COUNT) {
                    ++count

                    val karasuBoard = KarasuBoard(
                        initialKarasuPieces = karasuPieces,
                        initialFruitCount = fruitPieces,
                    )
                    val playerWins = karasuBoard.play(strategy)
                    if (playerWins) playerWinCount++
                }

                println("$karasuPieces $fruitPieces $strategyName $playerWinCount/$count ${playerWinCount / count.toFloat()}")
            }
        }
    }
}