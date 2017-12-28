package arrow

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class YonedaTest : UnitSpec() {

    val F = Yoneda.functor<IdHK>()

    val EQ = Eq<YonedaKind<IdHK, Int>> { a, b ->
        a.ev().lower() == b.ev().lower()
    }

    init {

        "instances can be resolved implicitly" {
            functor<YonedaKindPartial<IdHK>>() shouldNotBe null
        }

        testLaws(FunctorLaws.laws(F, { Yoneda(arrow.Id(it)) }, EQ))

        "toCoyoneda should convert to an equivalent Coyoneda" {
            forAll { x: Int ->
                val op = Yoneda(Id(x.toString()))
                val toYoneda = op.toCoyoneda().lower(Id.functor()).ev()
                val expected = Coyoneda(Id(x), Int::toString).lower(Id.functor()).ev()

                expected == toYoneda
            }
        }
    }
}
