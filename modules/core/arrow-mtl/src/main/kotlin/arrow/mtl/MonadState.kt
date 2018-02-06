package arrow.mtl

import arrow.*
import arrow.core.Tuple2
import arrow.typeclasses.Monad

@typeclass
interface MonadState<F, S> : Monad<F>, TC {

    fun <A> state(f: (S) -> Tuple2<S, A>): HK<F, A> = flatMap(get(), { s -> f(s).let { (a, b) -> map(set(a), { b }) } })

    fun get(): HK<F, S>

    fun set(s: S): HK<F, Unit>

    fun modify(f: (S) -> S): HK<F, Unit> = flatMap(get(), { s -> set(f(s)) })

    fun <A> inspect(f: (S) -> A): HK<F, A> = map(get(), f)
}