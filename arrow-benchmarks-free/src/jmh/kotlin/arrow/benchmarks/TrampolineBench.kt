package arrow.benchmarks

import arrow.core.Eval
import arrow.core.extensions.fx
import arrow.free.Trampoline
import arrow.free.TrampolineF
import arrow.free.extensions.fx
import arrow.free.flatMap
import arrow.free.map
import arrow.free.runT
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * Evaluate the performance of a Free / Trampoline algorithm.
 * These benchmarks use the calculation of Fibonacci sequences as support.
 * Note: to be compared to the Cats/Scala results.
 */
@State(Scope.Benchmark)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
open class TrampolineBench {

  @Param("30")
  var num = 0

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  fun trampoline(): Int = trampolineFibonacci(num).runT()

  /**
   * Note, the use of 'Mode.SingleShotTime' (i.e. no JVM warmup & single measurement).
   * To be compared to the memoized method...
   */
  @Benchmark
  @BenchmarkMode(Mode.SingleShotTime)
  fun trampolineSST(): Int = trampolineFibonacci(num).runT()

  /**
   * A trampolined calculation.
   */
  private fun trampolineFibonacci(n: Int): TrampolineF<Int> =
    when {
      n < 2 -> Trampoline.done(n)
      else -> {
        Trampoline.defer { trampolineFibonacci(n - 1) }
          .flatMap { x -> Trampoline.defer { trampolineFibonacci(n - 2) }
            .map { y -> x + y } }
      }
    }

  /**
   * Note, the use of 'Mode.SingleShotTime' (i.e. no JVM warmup & single measurement).
   * This allows not to propagate memoized data between tests... but, it relies on an imprecise calculation method.
   */
  @Benchmark
  @BenchmarkMode(Mode.SingleShotTime)
  fun eval(): Int = evalFibonacci(num).value()

  /**
   * A calculation using the data memoization.
   */
  private fun evalFibonacci(n: Int): Eval<Int> =
    when {
      n < 2 -> Eval.now(n)
      else -> {
        Eval.defer { evalFibonacci(n - 1) }
          .flatMap { x -> Eval.defer { evalFibonacci(n - 2) }
            .map { y -> x + y } }
      }
    }
}
