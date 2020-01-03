package com.jarhoax.multiplatform.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jarhoax.multiplatform.core.ExampleClass
import com.jarhoax.multiplatform.core.createApplicationScreenMessage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_text.text = createApplicationScreenMessage()

        val example = ExampleClass(this)
        example.doSomething("Hello World")
    }
}
