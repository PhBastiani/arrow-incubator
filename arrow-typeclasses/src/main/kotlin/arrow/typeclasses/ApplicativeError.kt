package arrow.typeclasses

import arrow.*
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right

@typeclass
interface ApplicativeError<F, E> : Applicative<F>, TC {

    fun <A> raiseError(e: E): HK<F, A>

    fun <A> handleErrorWith(fa: HK<F, A>, f: (E) -> HK<F, A>): HK<F, A>

    fun <A> handleError(fa: HK<F, A>, f: (E) -> A): HK<F, A> = handleErrorWith(fa) { pure(f(it)) }

    fun <A> attempt(fa: HK<F, A>): HK<F, Either<E, A>> =
            handleErrorWith(map(fa) { Right(it) }) {
                pure(Left(it))
            }

    fun <A> fromEither(fab: Either<E, A>): HK<F, A> = fab.fold({ raiseError<A>(it) }, { pure(it) })

    fun <A> catch(f: () -> A, recover: (Throwable) -> E): HK<F, A> =
            try {
                pure(f())
            } catch (t: Throwable) {
                raiseError<A>(recover(t))
            }
}