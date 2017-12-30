package arrow.data

import arrow.core.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec

@RunWith(KTestJUnitRunner::class)
class PartialFunctionTests : UnitSpec() {

    private val definetAt: (Int) -> Boolean = { it.rem(2) == 0 }
    private val body: (Int) -> String = {
        "is even"
    }

    init {

        "case syntax can be used to create partial functions" {
            val predicate = { n: Int -> n > 0 }
            val fa = case({ n: Int -> n > 0 } then { it * 2 })
            val fb = object : PartialFunction<Int, Int>() {
                override fun isDefinedAt(a: Int): Boolean = predicate(a)
                override fun invoke(p1: Int): Int = p1 * 2
            }
            fa(1) shouldBe fb(1)
            fa.isDefinedAt(-1) shouldBe false
        }

        "partial" {
            val isEven = PartialFunction(definetAt, body)

            (isEven.isDefinedAt(2)) shouldBe true
            isEven(2) shouldBe "is even"
        }

        "toPartialFunction"{
            val isEven = body.toPartialFunction(definetAt)
            (isEven.isDefinedAt(2)) shouldBe true
            isEven(2) shouldBe "is even"
        }

        "orElse" {
            val isEven = body.toPartialFunction(definetAt)
            val isOdd = { _: Int -> "is odd" }.toPartialFunction { !definetAt(it) }
            listOf(1, 2, 3).map(isEven orElse isOdd) shouldBe listOf("is odd", "is even", "is odd")
        }

        "invokeOrElse" {
            val isEven = body.toPartialFunction(definetAt)
            listOf(1, 2, 3).map { isEven.invokeOrElse(it, "is odd") } shouldBe listOf("is odd", "is even", "is odd")
        }

    }
}
