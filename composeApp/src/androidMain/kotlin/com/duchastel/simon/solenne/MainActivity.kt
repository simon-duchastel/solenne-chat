package com.duchastel.simon.solenne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.zacsweers.metro.createGraph
import com.duchastel.simon.solenne.di.ApplicationGraph
import com.duchastel.simon.solenne.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationComponent = createGraph<ApplicationGraph>()
        setContent {
            App(applicationComponent.circuit)
        }
    }
}
