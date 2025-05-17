package com.duchastel.simon.solenne.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.duchastel.simon.solenne.screens.splash.SplashScreen.State
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SplashUi(state: State, modifier: Modifier = Modifier) {
    SolenneScaffold(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
internal fun SplashUi_Preview() {
    SplashUi(
        state = State(),
        modifier = Modifier,
    )
}