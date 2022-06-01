package week1


fun main() {
    val sum = listOf(1, 3, 4).mySum()
    println(sum)
}

private fun List<Int>.mySum(): Int {

    var result = 0

    for (i in this) {
        result += i
    }

    return result
}
