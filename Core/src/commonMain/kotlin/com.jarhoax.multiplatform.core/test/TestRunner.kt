package com.jarhoax.multiplatform.core.test

import ExampleApi

class TestRunner(apiInstanceFactory: () -> ExampleApi) {
    val exampleTestSuite: ExampleTestSuite = ExampleTestSuite(apiInstanceFactory)
}
