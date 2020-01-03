package com.jarhoax.multiplatform.core

import platform.Foundation.NSLog

class ExampleClass : ExampleApi {

    override fun doSomething(param: String) {
        // Just logging the param here as an example. A logger could be implemented more easily with
        // expect <> actual
        NSLog(param)
    }
}
