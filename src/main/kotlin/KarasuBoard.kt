import kotlin.random.Random

class KarasuBoard(
    initialFruitCount: Int = 10,
    initialKarasuPieces: Int = 9,
) {
    val fruits: Array<Int> = Array(4) { initialFruitCount }
    var remainingKarasuPieces = initialKarasuPieces

    val isFinished get() = playerWinws || karasuWinws

    val playerWinws get() = fruits.all { it == 0 }

    val karasuWinws get() = remainingKarasuPieces == 0

    fun step(chooseTwo: (KarasuBoard) -> Pair<Int, Int>) {
        if (isFinished) return

        when (val dice = Random.nextInt(6)) {
            0, 1, 2, 3 -> {
                removeFruit(dice)
            }

            4 -> {
                val fruitsToRemove = chooseTwo(this)
                removeFruit(fruitsToRemove.first)
                removeFruit(fruitsToRemove.second)
            }

            5 -> {
                --remainingKarasuPieces
            }
        }
    }

    private fun removeFruit(fruitToRemove: Int) {
        if (fruitToRemove in 0..3 && fruits[fruitToRemove] > 0)
            fruits[fruitToRemove]--
    }
}

fun KarasuBoard.play(chooseTwo: (KarasuBoard) -> Pair<Int, Int>): Boolean {
    while (!isFinished) {
        step(chooseTwo)
    }
    return playerWinws
}

fun KarasuBoard.println() {
    println("F: ${fruits.joinToString(",")} K: $remainingKarasuPieces")
}

val KarasuBoard.availableFruitsIndexes
    get() = fruits.indices.asSequence()
        .filter { fruits[it] > 0 }
        .ifEmpty { sequenceOf(0) }

val randomFruitsStrategy: (KarasuBoard) -> Pair<Int, Int> = {
    Pair(
        it.availableFruitsIndexes.shuffled().first(),
        it.availableFruitsIndexes.shuffled().first()
    )
}

val minFruitsStrategy: (KarasuBoard) -> Pair<Int, Int> = { karasuBoard ->
    Pair(
        karasuBoard.availableFruitsIndexes.minBy { karasuBoard.fruits[it] },
        karasuBoard.availableFruitsIndexes.minBy { karasuBoard.fruits[it] },
    )
}

val maxFruitsStrategy: (KarasuBoard) -> Pair<Int, Int> = { karasuBoard ->
    Pair(
        karasuBoard.availableFruitsIndexes.maxBy { karasuBoard.fruits[it] },
        karasuBoard.availableFruitsIndexes.maxBy { karasuBoard.fruits[it] },
    )
}

val firstFruitsStrategy: (KarasuBoard) -> Pair<Int, Int> = { karasuBoard ->
    Pair(
        karasuBoard.availableFruitsIndexes.first(),
        karasuBoard.availableFruitsIndexes.first(),
    )
}

val zeroFruitsStrategy: (KarasuBoard) -> Pair<Int, Int> = { karasuBoard ->
    Pair(
        0,
        0,
    )
}

val strategies = listOf(
    Pair("random", randomFruitsStrategy),
    Pair("min", minFruitsStrategy),
    Pair("max", maxFruitsStrategy),
    Pair("first", firstFruitsStrategy),
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