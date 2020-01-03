package com.jarhoax.multiplatform.core.test

import com.jarhoax.multiplatform.core.ExampleApi

class TestRunner(apiInstanceFactory: () -> ExampleApi) {
    val exampleTestSuite: ExampleTestSuite = ExampleTestSuite(apiInstanceFactory)
}
