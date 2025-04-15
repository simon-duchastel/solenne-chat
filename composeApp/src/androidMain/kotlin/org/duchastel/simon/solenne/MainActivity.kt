package org.duchastel.simon.solenne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.zacsweers.metro.createGraph
import org.duchastel.simon.solenne.di.ApplicationGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationComponent = createGraph<ApplicationGraph>()
        setContent {
            App(applicationComponent.circuit)
        }
    }
}
