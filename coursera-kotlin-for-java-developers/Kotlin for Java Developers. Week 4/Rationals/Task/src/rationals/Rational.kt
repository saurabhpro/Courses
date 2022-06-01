package rationals

import java.math.BigInteger

private const val FORWARD_SLASH = "/"
private const val MINUS = "-"

data class Rational(val numerator: BigInteger, val denominator: BigInteger) {

    operator fun plus(rational: Rational): Rational {
        val b = this.denominator * rational.denominator
        val a = ((b / this.denominator) * this.numerator) + ((b / rational.denominator) * rational.numerator)
        return Rational(a, b)
    }

    operator fun minus(rational: Rational): Rational {
        val b = this.denominator * rational.denominator
        val a = ((b / this.denominator) * this.numerator) - ((b / rational.denominator) * rational.numerator)
        return Rational(a, b)
    }

    operator fun times(rational: Rational): Rational {
        val b = this.denominator * rational.denominator
        val a = this.numerator * rational.numerator
        return Rational(a, b)
    }

    operator fun div(rational: Rational): Rational {
        val b = this.denominator * rational.numerator
        val a = this.numerator * rational.denominator
        return Rational(a, b)
    }

    operator fun unaryMinus(): Rational {
        val b = this.denominator
        val a = -this.numerator
        return Rational(a, b)
    }

    operator fun compareTo(rational: Rational): Int {
        if (this.denominator == rational.denominator) {
            return when {
                this.numerator == rational.numerator -> 0
                this.numerator < rational.numerator -> -1
                else -> 1
            }
        }
        else {
            val a = this.numerator * rational.denominator
            val b = rational.numerator * this.denominator
            return when {
                a == b -> 0
                a < b -> -1
                else -> 1
            }
        }
    }

    operator fun rangeTo(rational: Rational): Pair<Rational, Rational> {
        return Pair(this, rational)
    }

    private fun rationalToStringConverter(a: BigInteger, b: BigInteger): String {
        if (b == 1.toBigInteger()) return a.toString()

        val gcd = a.gcd(b)

        if (b / gcd == 1.toBigInteger()) return (a / gcd).toString()

        if ((MINUS in (a / gcd).toString() && MINUS in (b / gcd).toString())
            || MINUS in (b / gcd).toString()) {
            return (-a / gcd).toString() + FORWARD_SLASH + (-b / gcd).toString()
        }

        return (a / gcd).toString() + FORWARD_SLASH + (b / gcd).toString()
    }

    override fun toString(): String = rationalToStringConverter(numerator, denominator)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (this.toString() != other.toString()) return false
        return true
    }

    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }
}

infix fun Number.divBy(divisor: Number): Rational {

    fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)
    fun Int.toBigInteger(): BigInteger = BigInteger.valueOf(toLong())

    when (divisor) {
        is Int -> return Rational(this.toInt().toBigInteger(), divisor.toBigInteger())
        is Long -> return Rational(this.toLong().toBigInteger(), divisor.toBigInteger())
    }

    return Rational(this as BigInteger, divisor as BigInteger)
}

fun String.toRational(): Rational {
    val s = this.split(FORWARD_SLASH)

    if (s.size == 1) return Rational(s[0].toBigInteger(), 1.toBigInteger())

    return Rational(s[0].toBigInteger(), s[1].toBigInteger())
}

operator fun Pair<Rational, Rational>.contains(rational: Rational): Boolean {
    if (rational >= this.first && rational <= this.second) return true
    return false
}

fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println(
        "912016490186296920119201192141970416029".toBigInteger() divBy
                "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2
    )
}