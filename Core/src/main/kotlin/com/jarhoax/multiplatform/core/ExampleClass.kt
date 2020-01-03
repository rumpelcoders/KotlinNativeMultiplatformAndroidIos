package com.jarhoax.multiplatform.core

import android.content.Context
import android.util.Log

class ExampleClass constructor(private val context: Context) : ExampleApi {
   
    override fun doSomething(param: String) {
        // Just logging the param here as an example. A logger could be implemented more easily with
        // expect <> actual
        Log.d("DEBUG", param)
    }
}
