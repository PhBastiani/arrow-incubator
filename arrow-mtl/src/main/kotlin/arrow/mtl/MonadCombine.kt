package arrow.mtl

import arrow.*
import arrow.core.Tuple2
import arrow.typeclasses.*

/**
 * The combination of a Monad with a MonoidK
 */
@typeclass
interface MonadCombine<F> : MonadFilter<F>, Alternative<F>, TC {

    fun <G, A> unite(fga: HK<F, HK<G, A>>, FG: Foldable<G>): HK<F, A> =
            flatMap(fga, { ga -> FG.foldLeft(ga, empty<A>(), { acc, a -> combineK(acc, pure(a)) }) })

    fun <G, A, B> separate(fgab: HK<F, HK2<G, A, B>>, BFG: Bifoldable<G>): Tuple2<HK<F, A>, HK<F, B>> {
        val asep = flatMap(fgab, { gab -> BFG.bifoldMap(gab, { pure(it) }, { _ -> empty<A>() }, algebra<A>()) })
        val bsep = flatMap(fgab, { gab -> BFG.bifoldMap(gab, { _ -> empty<B>() }, { pure(it) }, algebra<B>()) })
        return Tuple2(asep, bsep)
    }
}

inline fun <F, reified G, A> MonadCombine<F>.uniteF(fga: HK<F, HK<G, A>>, FG: Foldable<G> = foldable()) = unite(fga, FG)

inline fun <F, reified G, A, B> MonadCombine<F>.separateF(fgab: HK<F, HK2<G, A, B>>, BFG: Bifoldable<G> = bifoldable()) = separate(fgab, BFG)
