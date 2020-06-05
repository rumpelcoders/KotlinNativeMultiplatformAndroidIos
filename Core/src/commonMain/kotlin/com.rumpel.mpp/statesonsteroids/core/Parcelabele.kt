package com.rumpel.mpp.statesonsteroids.core

// Common Code
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()


// Common Code
expect interface Parcelable
