package nicestring

fun String.isNice(): Boolean {

    val noBadSubstring = setOf("bu", "ba", "be").none { this.contains(it) }

    val hasThreeVowels = count { it in "aeiou" } >= 3

    val hasDouble = zipWithNext().any { it.first == it.second }

    return listOf(noBadSubstring, hasDouble, hasThreeVowels).count { it } >= 2
}