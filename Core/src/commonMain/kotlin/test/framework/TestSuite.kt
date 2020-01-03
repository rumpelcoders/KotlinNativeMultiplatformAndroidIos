package com.jarhoax.multiplatform.core.test.framework

interface TestSuite {
    val tests: ArrayList<() -> TestResult>

    fun execute(): ArrayList<TestResult> {
        val result = ArrayList<TestResult>()
        for (test in tests) {
            result.add(test())
        }
        return result
    }
}
