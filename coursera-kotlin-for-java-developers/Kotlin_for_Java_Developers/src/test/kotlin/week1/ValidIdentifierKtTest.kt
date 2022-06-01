package week1

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal class ValidIdentifierKtTest {

    @Test
    fun isValidIdentifier() {
        assert(isValidIdentifier("name"))   // true
        assert(isValidIdentifier("_name"))  // true
        assert(isValidIdentifier("_12"))    // true
        assertFalse(isValidIdentifier(""))       // false
        assertFalse(isValidIdentifier("012"))    // false
        assertFalse(isValidIdentifier("no$"))    // false

        println(BigDecimal(0.1).divide(BigDecimal(365), MathContext.DECIMAL64))
        println(ChronoUnit.DAYS.between(LocalDate.of(2022, 10, 26), LocalDate.of(2023, 1, 24)))
    }
}