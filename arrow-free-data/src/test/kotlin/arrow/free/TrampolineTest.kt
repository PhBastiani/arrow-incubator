package arrow.free

import arrow.free.extensions.fx
import io.kotlintest.shouldBe

class TrampolineTest : UnitSpec() {

  init {
    "trampoline over 10000 should return false and not break the stack" {
      odd(10000).runT() shouldBe false
    }

    "trampoline over 10001 should return true and not break the stack" {
      odd(10001).runT() shouldBe true
    }

    "trampoline should support fx syntax" {
      tryfxsyntax(10000).runT() shouldBe true
    }

    "defer should be lazy" {
      forall { x: TrampolineF<Int> ->
        Trampoline.defer { x } shouldBe x
      }
    }

    "defer should be lazy again" {
      // this shouldn't throw an exception unless we try to run it
      Trampoline.defer<Int> { throw RuntimeException("blablabla") }
    }
  }

  private fun odd(n: Int): TrampolineF<Boolean> {
    return when (n) {
      0 -> Trampoline.done(false)
      else -> {
        Trampoline.defer { even(n - 1) }
      }
    }
  }

  private fun even(n: Int): TrampolineF<Boolean> {
    return when (n) {
      0 -> Trampoline.done(true)
      else -> {
        Trampoline.defer { odd(n - 1) }
      }
    }
  }

  private fun tryfxsyntax(n: Int): TrampolineF<Boolean> =
    TrampolineF.fx {
      val x = Trampoline.defer { odd(10000) }.bind()
      val y = Trampoline.defer { even(10000) }.bind()
      x xor y
    }
}
