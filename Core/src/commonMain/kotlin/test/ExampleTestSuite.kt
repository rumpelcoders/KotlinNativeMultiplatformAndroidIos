package com.jarhoax.multiplatform.core.test

import com.jarhoax.multiplatform.core.ExampleApi
import com.jarhoax.multiplatform.core.test.framework.TestResult
import com.jarhoax.multiplatform.core.test.framework.TestSuite

class ExampleTestSuite(apiInstanceFactory: () -> ExampleApi): TestSuite {
    override var tests: ArrayList<() -> TestResult> = ArrayList()
        private set

    init {
        tests.add {
            val unitUnderTest = apiInstanceFactory()

            // TODO: add test logic

            TestResult(true)
        }
        tests.add {
            val unitUnderTest = apiInstanceFactory()

            // TODO: add test logic

            TestResult(true)
        }
    }
}
