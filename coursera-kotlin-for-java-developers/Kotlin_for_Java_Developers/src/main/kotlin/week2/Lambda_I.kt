package week2

import week2.Gender.FEMALE
import week2.Gender.MALE


data class Hero(val name: String, val age: Int, val gender: Gender?)

enum class Gender { MALE, FEMALE }

val heroes = listOf(
    Hero("The Captain", 60, MALE),
    Hero("Frenchy", 42, MALE),
    Hero("The Kid", 9, null),
    Hero("Lady Lauren", 29, FEMALE),
    Hero("First Mate", 29, MALE),
    Hero("Sir Stephen", 37, MALE)
)

fun main() {
    println(heroes.last().name)

    println(heroes.firstOrNull { it.age == 30 }?.name)

    println(heroes.map { it.age }.distinct().size)

    println(heroes.filter { it.age < 30 }.size)

    // divide into 2 list
    val (youngest, oldest) = heroes.partition { it.age < 30 }
    println("$oldest $oldest.size")

    println(heroes.maxByOrNull { it.age }?.name)

    println(heroes.all { it.age < 50 })
    println(heroes.any { it.gender == FEMALE })


    val mapByAge: Map<Int, List<Hero>> = heroes.groupBy { it.age }
    val (age, group) = mapByAge.maxByOrNull { (_, group) ->
        group.size
    }!!
    println(age)

    val mapByName: Map<String, Hero> = heroes.associateBy { it.name }
    mapByName["Frenchy"]?.age
}