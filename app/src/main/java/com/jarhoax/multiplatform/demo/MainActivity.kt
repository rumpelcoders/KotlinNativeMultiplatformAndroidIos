package com.jarhoax.multiplatform.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jarhoax.multiplatform.core.SlackApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SlackApi().about {
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    main_text.text = it
                }
            }
        }
        setContentView(R.layout.activity_main)
    }
}
