package cats.benchmarks

import cats._
import cats.implicits._
import cats.free.Trampoline
import scala.util.control.TailCalls
import org.openjdk.jmh.annotations._
import java.util.concurrent.TimeUnit

/**
 * Evaluate the performance of a Free / Trampoline algorithm.
 * These benchmarks use the calculation of Fibonacci sequences as support.
 * Please use the Cats/Scala results as reference for Arrow/Kotlin benchmarks.
 */
@State(Scope.Benchmark)
@Fork(2)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@CompilerControl(CompilerControl.Mode.DONT_INLINE)
class TrampolineBench {

  @Param(value = Array("30"))
  var num: Int = _

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  def trampoline(): Int = trampolineFibonacci(num).run

  /**
   * Note, the use of 'Mode.SingleShotTime' (i.e. no JVM warmup & single measurement).
   * To be compared to the memoized method...
   */
  @Benchmark
  @BenchmarkMode(Array(Mode.SingleShotTime))
  def trampolineSST(): Int = trampolineFibonacci(num).run

  /**
   * A trampolined calculation.
   */
  private def trampolineFibonacci(n: Int): Trampoline[Int] =
    if (n < 2) Trampoline.done(n)
    else
      Trampoline.defer(trampolineFibonacci(n - 1))
        .flatMap(x => Trampoline.defer(trampolineFibonacci(n - 2))
          .map(y => x + y))

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  def stdlib(): Int = stdlibFibonacci(num).result

  /**
   * A calculation using the tail recursion.
   */
  private def stdlibFibonacci(n: Int): TailCalls.TailRec[Int] =
    if (n < 2) TailCalls.done(n)
    else
      TailCalls.tailcall(stdlibFibonacci(n - 1))
        .flatMap(x => TailCalls.tailcall(stdlibFibonacci(n - 2))
          .map(y => x + y))

  /**
   * Note, the use of 'Mode.SingleShotTime' (i.e. no JVM warmup & single measurement).
   * This allows not to propagate memoized data between tests... but, it relies on an imprecise calculation method.
   */
  @Benchmark
  @BenchmarkMode(Array(Mode.SingleShotTime))
  def eval(): Int = evalFibonacci(num).value

  /**
   * A calculation using the data memoization.
   */
  private def evalFibonacci(n: Int): Eval[Int] =
    if (n < 2) Eval.now(n)
    else
      Eval.defer(evalFibonacci(n - 1))
        .flatMap(x => Eval.defer(evalFibonacci(n - 2))
          .map(y => x + y))
}