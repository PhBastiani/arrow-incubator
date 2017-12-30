package arrow.typeclasses

import arrow.HK
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

interface BindingInContextContinuation<in T> : Continuation<T> {
    fun await(): Throwable?
}

@RestrictsSuspension
open class MonadContinuation<F, A>(M: Monad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        Continuation<HK<F, A>>, Monad<F> by M {

    override fun resume(value: HK<F, A>) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    protected fun bindingInContextContinuation(context: CoroutineContext): BindingInContextContinuation<HK<F, A>> =
            object : BindingInContextContinuation<HK<F, A>> {
                val latch: CountDownLatch = CountDownLatch(1)

                var error: Throwable? = null

                override fun await() = latch.await().let { error }

                override val context: CoroutineContext = context

                override fun resume(value: HK<F, A>) {
                    returnedMonad = value
                    latch.countDown()
                }

                override fun resumeWithException(exception: Throwable) {
                    error = exception
                    latch.countDown()
                }
            }

    protected lateinit var returnedMonad: HK<F, A>

    open fun returnedMonad(): HK<F, A> = returnedMonad

    suspend fun <B> HK<F, B>.bind(): B = bind { this }

    suspend fun <B> (() -> HK<F, B>).bindInM(context: CoroutineContext): B =
            bindInM(context, this)

    suspend fun <B> (() -> B).bindIn(context: CoroutineContext): B =
            bindInM(context, { pure(this()) })

    open suspend fun <B> bind(m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = flatMap(m(), { x: B ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }

    suspend fun <B> bindIn(context: CoroutineContext, m: () -> B): B =
            bindInM(context, { pure(m()) })

    open suspend fun <B> bindInM(context: CoroutineContext, m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        val monadCreation: suspend () -> HK<F, A> = {
            flatMap(m(), { xx: B ->
                c.stackLabels = labelHere
                c.resume(xx)
                returnedMonad
            })
        }
        val completion = bindingInContextContinuation(context)
        returnedMonad = flatMap(pure(Unit), {
            monadCreation.startCoroutine(completion)
            val error = completion.await()
            if (error != null) {
                throw error
            }
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }

    infix fun <B> yields(b: B): HK<F, B> = yields { b }

    infix fun <B> yields(b: () -> B): HK<F, B> = pure(b())
}
