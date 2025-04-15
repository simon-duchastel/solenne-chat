package org.duchastel.simon.solenne.parcel

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()

// necessary to shim the Android Parcelable class for Circuit
expect interface Parcelable
