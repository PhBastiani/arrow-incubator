package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SequenceKOf
import arrow.core.Tuple2
import arrow.core.fix
import arrow.extension
import arrow.mtl.typeclasses.MonadCombine
import arrow.mtl.typeclasses.MonadFilter
import arrow.core.combineK as sequenceCombineK

@extension
interface SequenceKMonadFilter : MonadFilter<ForSequenceK> {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A, B> Kind<ForSequenceK, A>.mapFilter(f: (A) -> Option<B>): SequenceK<B> =
    fix().mapFilter(f)

  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.flatMap(f: (A) -> Kind<ForSequenceK, B>): SequenceK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, SequenceKOf<Either<A, B>>>): SequenceK<B> =
    SequenceK.tailRecM(a, f)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@extension
interface SequenceKMonadCombine : MonadCombine<ForSequenceK> {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A, B> Kind<ForSequenceK, A>.mapFilter(f: (A) -> Option<B>): SequenceK<B> =
    fix().mapFilter(f)

  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.flatMap(f: (A) -> Kind<ForSequenceK, B>): SequenceK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, SequenceKOf<Either<A, B>>>): SequenceK<B> =
    SequenceK.tailRecM(a, f)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)

  override fun <A> Kind<ForSequenceK, A>.combineK(y: Kind<ForSequenceK, A>): SequenceK<A> =
    fix().sequenceCombineK(y)
}
