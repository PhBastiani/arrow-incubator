package arrow

interface Semigroup<A> : Typeclass {
    /**
     * Combine two [A] values.
     */
    fun combine(a: A, b: A): A

    fun maybeCombine(a: A, b: A?): A = Option.fromNullable(b).fold({ a }, { this.combine(a, it) })

}

inline fun <reified A> A.combine(b: A, FT: Semigroup<A> = semigroup()): A = FT.combine(this, b)

inline fun <reified A> semigroup(): Semigroup<A> = instance(InstanceParametrizedType(Semigroup::class.java, listOf(typeLiteral<A>())))
