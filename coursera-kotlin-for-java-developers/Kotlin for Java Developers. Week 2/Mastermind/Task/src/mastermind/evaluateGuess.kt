package mastermind

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

private fun String.replaceAtIndex(index: Int, replaceWith: CharSequence): String {
    return this.replaceRange(index, index + 1, replaceWith)
}

fun evaluateGuess(secret: String, guess: String): Evaluation {
    var mutableSecret = secret
    var mutableGuess = guess
    var rightPosition = 0
    var wrongPosition = 0

    for (index in secret.indices) {
        if (secret[index] == guess[index]) {
            rightPosition++
            mutableSecret = mutableSecret.replaceAtIndex(index, "_")
            mutableGuess = mutableGuess.replaceAtIndex(index, "_")
        }
    }

    for (index in secret.indices) {
        if (mutableGuess[index] != '_' && mutableSecret.contains(mutableGuess[index])) {
            var j = 0
            while (mutableSecret[j] != mutableGuess[index]) {
                j++
            }
            mutableSecret = mutableSecret.replaceAtIndex(j, "_")
            wrongPosition++
        }

    }

    return Evaluation(rightPosition, wrongPosition)
}
