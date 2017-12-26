package arrow.data

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.*
import arrow.laws.EqLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SequenceKWTest : UnitSpec() {
    val applicative = SequenceKW.applicative()

    init {

        "instances can be resolved implicitly" {
            functor<SequenceKWHK>() shouldNotBe null
            applicative<SequenceKWHK>() shouldNotBe null
            monad<SequenceKWHK>() shouldNotBe null
            foldable<SequenceKWHK>() shouldNotBe null
            traverse<SequenceKWHK>() shouldNotBe null
            semigroupK<SequenceKWHK>() shouldNotBe null
            monoidK<SequenceKWHK>() shouldNotBe null
            semigroup<SequenceKW<Int>>() shouldNotBe null
            monoid<SequenceKW<Int>>() shouldNotBe null
            eq<SequenceKW<Int>>() shouldNotBe null
        }

        val eq: Eq<HK<SequenceKWHK, Int>> = object : Eq<HK<SequenceKWHK, Int>> {
            override fun eqv(a: HK<SequenceKWHK, Int>, b: HK<SequenceKWHK, Int>): Boolean =
                    a.toList() == b.toList()
        }

        testLaws(
            EqLaws.laws { sequenceOf(it).k() },
            MonadLaws.laws(SequenceKW.monad(), eq),
            MonoidKLaws.laws(SequenceKW.monoidK(), applicative, eq),
            TraverseLaws.laws(SequenceKW.traverse(), applicative, { n: Int -> SequenceKW(sequenceOf(n)) }, eq)
        )
    }
}
