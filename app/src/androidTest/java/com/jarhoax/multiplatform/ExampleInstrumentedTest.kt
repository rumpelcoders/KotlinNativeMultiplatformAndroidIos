package com.jarhoax.multiplatform

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jarhoax.multiplatform.core.ExampleClass
import com.jarhoax.multiplatform.core.test.TestRunner

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.jarhoax.multiplatform", appContext.packageName)
    }

    @Test
    fun testBitmovinPlayer() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val runner = TestRunner { ExampleClass(appContext) }

        val results = runner.exampleTestSuite.execute()
        for (result in results) {
            assertTrue(result.message, result.success)
        }
    }
}
