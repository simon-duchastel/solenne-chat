package com.duchastel.simon.solenne.util.types

/**
 * A result that mimics Kotlin's [Result] type but allows for
 * type coercion, ex. in when statements. Checking that a [SolenneResult]
 * is a success causes the compiler to cast that type as a success.
 */
sealed class SolenneResult<out T>(private val value: T?) {
    /**
     * Returns the value if it's a success or null otherwise.
     */
    open operator fun invoke(): T? = value

    /**
     * Useful for when non-operator syntax is more idiomatic.
     */
    open fun getOrNull(): T? = value
}

/**
 * Represents a success. The value of this type is always non-null
 * (unless the type itself is nullable).
 */
data class Success<out T>(private val value: T) : SolenneResult<T>(value = value) {
    override operator fun invoke(): T = value
    override fun getOrNull(): T = value
}

/**
 * Represents a failure. The value of this type is always null. Optionally the cause
 * of the failure may be provided as a [Throwable].
 */
data class Failure<out T>(val error: Throwable?) : SolenneResult<T>(value = null) {
    override operator fun invoke(): T? = null
}

/**
 * Calls [block] with the value of the result if this is a [Success], otherwise does nothing.
 */
inline fun <T> SolenneResult<T>.onSuccess(block: (T) -> Unit): SolenneResult<T> {
    return when (this) {
        is Failure -> {
            this
        }
        is Success -> {
            block(this())
            this
        }
    }
}

/**
 * Calls [block] with the cause of the failure if this is a [Failure], otherwise does nothing.
 */
inline fun <T> SolenneResult<T>.onFailure(block: (Throwable?) -> Unit): SolenneResult<T> {
    return when (this) {
        is Failure -> {
            block(this.error)
            this
        }
        is Success -> this
    }
}

/**
 * Maps the result of this to a new type if [Success], otherwise propagates the [Failure].
 */
fun <T, R> SolenneResult<T>.map(block: (T) -> R): SolenneResult<R> {
    return when (this) {
        is Success -> block(this()).asSuccess()
        is Failure -> Failure(this.error)
    }
}

/**
 * Returns result wrapped in a [SolenneResult] as a [Success].
 */
fun <T> T.asSuccess(): SolenneResult<T> = Success(this)

/**
 * Returns result wrapped in a [SolenneResult] as a [Failure].
 */
fun <T> Throwable?.asFailure(): SolenneResult<T> = Failure(this)
