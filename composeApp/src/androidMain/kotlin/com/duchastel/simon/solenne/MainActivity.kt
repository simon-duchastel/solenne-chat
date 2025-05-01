package com.duchastel.simon.solenne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.zacsweers.metro.createGraphFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationGraph = createGraphFactory<AndroidApplicationGraph.Factory>()
            .create(this.applicationContext)

        setContent {
            App(applicationGraph.circuit)
        }
    }
}
