package week5

fun m(i: Int): Int {
    print("m$i ")
    return i
}

fun f(i: Int): Boolean {
    print("f$i ")
    return i % 2 == 0
}

fun main() {
    val list = listOf(1, 2, 3, 4)
    println(list.map(::m).filter(::f))  //m1 m2 m3 m4 f1 f2 f3 f4
    print(list.asSequence().map(::m).filter(::f).toList()) //?
}


