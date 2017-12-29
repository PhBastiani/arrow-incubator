package arrow

import arrow.core.Id
import arrow.core.Option
import arrow.core.Some
import arrow.data.NonEmptyList
import arrow.free.Free
import arrow.free.foldMap
import arrow.free.instances.FreeEq
import arrow.free.instances.FreeMonadInstance
import arrow.instances.FreeEq
import arrow.instances.monad
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.MonadLaws
import arrow.typeclasses.applicative
import arrow.typeclasses.functor
import arrow.typeclasses.monad

sealed class Ops<out A> : HK<Ops.F, A> {

    class F private constructor()

    data class Value(val a: Int) : Ops<Int>()
    data class Add(val a: Int, val y: Int) : Ops<Int>()
    data class Subtract(val a: Int, val y: Int) : Ops<Int>()

    companion object : FreeMonadInstance<F> {
        fun value(n: Int): Free<F, Int> = Free.liftF(Ops.Value(n))
        fun add(n: Int, y: Int): Free<F, Int> = Free.liftF(Ops.Add(n, y))
        fun subtract(n: Int, y: Int): Free<F, Int> = Free.liftF(Ops.Subtract(n, y))
    }
}

fun <A> HK<Ops.F, A>.ev(): Ops<A> = this as Ops<A>

@RunWith(KTestJUnitRunner::class)
class FreeTest : UnitSpec() {

    private val program = arrow.test.laws.ev()

    private fun stackSafeTestProgram(n: Int, stopAt: Int): Free<Ops.F, Int> = arrow.test.laws.ev()

    init {

        "instances can be resolved implicitly" {
            functor<FreeKindPartial<OpsAp.F>>() shouldNotBe null
            applicative<FreeKindPartial<OpsAp.F>>()  shouldNotBe null
            monad<FreeKindPartial<OpsAp.F>>()  shouldNotBe null
        }

        val EQ: FreeEq<Ops.F, IdHK, Int> = FreeEq(idInterpreter)
        testLaws(
            EqLaws.laws<Free<Ops.F, Int>>(EQ, { Ops.value(it) }),
            MonadLaws.laws(Ops, EQ)
        )

        "Can interpret an ADT as Free operations" {
            program.foldMap(optionInterpreter, Option.monad()).ev() shouldBe Some(-30)
            program.foldMap(idInterpreter, Id.monad()).ev() shouldBe Id(-30)
            program.foldMap(nonEmptyListInterpreter, NonEmptyList.monad()).ev() shouldBe NonEmptyList.of(-30)
        }

        "foldMap is stack safe" {
            val n = 50000
            val hugeProg = stackSafeTestProgram(0, n)
            hugeProg.foldMap(idInterpreter, Id.monad()).value() shouldBe n
        }

    }
}
