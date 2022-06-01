package week2

fun main() {
    val s = "saurabh"
    println(s as? Int)    // null
    println(s as Int?)    // class cast exception
}