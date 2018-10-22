package arrow.mtl.instances

import arrow.Kind
import arrow.core.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.mtl.typeclasses.MonadFilter
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.Applicative
import arrow.instances.traverse as optionTraverse
import arrow.instances.traverseFilter as optionTraverseFilter

@extension
interface OptionTraverseFilterInstance : TraverseFilter<ForOption> {
  override fun <A> Kind<ForOption, A>.filter(f: (A) -> Boolean): Option<A> =
    fix().filter(f)

  override fun <G, A, B> Kind<ForOption, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Option<B>> =
    optionTraverseFilter(AP, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <G, A, B> Kind<ForOption, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Option<B>> =
    optionTraverse(AP, f)

  override fun <A> Kind<ForOption, A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForOption, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForOption, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> OptionOf<A>.forAll(p: (A) -> Boolean): Boolean =
    fix().forall(p)

  override fun <A> Kind<ForOption, A>.isEmpty(): Boolean =
    fix().isEmpty()

  override fun <A> Kind<ForOption, A>.nonEmpty(): Boolean =
    fix().nonEmpty()
}

@extension
interface OptionMonadFilterInstance : MonadFilter<ForOption> {
  override fun <A> empty(): Option<A> =
    Option.empty()

  override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForOption, A>.flatMap(f: (A) -> Kind<ForOption, B>): Option<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, OptionOf<Either<A, B>>>): Option<B> =
    Option.tailRecM(a, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)

  override fun <A> Kind<ForOption, A>.filter(f: (A) -> Boolean): Option<A> =
    fix().filter(f)
}

object OptionMtlContext : OptionMonadFilterInstance, OptionTraverseFilterInstance {
  override fun <A> Kind<ForOption, A>.filter(f: (A) -> Boolean): Option<A> =
    fix().filter(f)

  override fun <A, B> Kind<ForOption, A>.mapFilter(f: (A) -> Option<B>): Option<B> =
    flatMap { a -> f(a).fold({ empty<B>() }, { just(it) }) }

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)
}

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForOption.Companion.extensions(f: OptionMtlContext.() -> A): A =
  f(OptionMtlContext)
