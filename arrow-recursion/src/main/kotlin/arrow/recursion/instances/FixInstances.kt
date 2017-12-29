package arrow.recursion.instances

import arrow.*
import arrow.recursion.*
import arrow.recursion.typeclass.Birecursive
import arrow.typeclasses.Functor
import arrow.typeclasses.Nested

@instance(Fix::class)
interface FixInstances<F> : Birecursive<FixHK, F> {
    fun FF(): Functor<F>

    override fun projectT(fg: FixKind<F>): HK<Nested<FixHK, F>, FixKind<F>> =
            fg.ev().projectT()

    override fun embedT(compFG: HK<Nested<FixHK, F>, FixKind<F>>): FixKind<F> =
            Fix.embedT(compFG, FF())
}