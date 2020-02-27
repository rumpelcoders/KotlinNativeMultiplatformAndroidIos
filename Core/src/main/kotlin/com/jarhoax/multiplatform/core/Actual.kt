package com.jarhoax.multiplatform.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun platformName(): String {
    return "Android"
}


internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Default