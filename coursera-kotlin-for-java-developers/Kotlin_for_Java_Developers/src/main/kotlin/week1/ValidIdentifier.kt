package week1

fun isValidIdentifier(s: String): Boolean {
    fun isAllowedCharacter(ch: Char): Boolean = ch == '_' || ch.isLetterOrDigit()

    if (s.isEmpty() || s[0].isDigit()) return false

    for (ch in s) {
        if (!isAllowedCharacter(ch)) return false
    }

    return true
}