package com.duchastel.simon.solenne.parcel

/**
 * This is necessary for Circuit to work on Android, since CircuitScreens
 * are expected to have an Android `Parcelize` annotation.
 * We use the `additionalAnnotation` Parcelize compiler flag to make this work.
 */
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()

expect interface Parcelable