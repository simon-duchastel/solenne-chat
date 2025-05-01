package com.duchastel.simon.solenne

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen
import com.slack.circuit.backstack.NavArgument
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.animation.AnimatedNavDecorator
import com.slack.circuit.foundation.internal.BackHandler
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.ExperimentalCircuitApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.slack.circuit.foundation.animation.AnimatedNavEvent
import com.slack.circuit.foundation.animation.AnimatedNavState
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import kotlinx.collections.immutable.ImmutableList

data class CustomNavState<T : NavArgument>(
  val args: ImmutableList<T>,
  override val backStackDepth: Int,
  override val screen: Screen = args.first().screen,
  override val rootScreen: Screen = args.last().screen,
) : AnimatedNavState

class CustomAnimatedNavDecoratorFactory : AnimatedNavDecorator.Factory {
  override fun <T : NavArgument> create(): AnimatedNavDecorator<T, *> {
    return CustomDecorator()
  }
}

class CustomDecorator<T : NavArgument> : AnimatedNavDecorator<T, CustomNavState<T>> {
    override fun AnimatedContentTransitionScope<AnimatedNavState>.transitionSpec(
        animatedNavEvent: AnimatedNavEvent
    ): ContentTransform {
        // TODO: Replace with actual transitions
        return slideInVertically() + fadeIn() togetherWith  slideOutVertically() + fadeOut()
    }

    override fun targetState(args: ImmutableList<T>, backStackDepth: Int): CustomNavState<T> {
        return CustomNavState(args, backStackDepth)
    }

    @Composable
    override fun updateTransition(
        args: ImmutableList<T>,
        backStackDepth: Int,
    ): Transition<CustomNavState<T>> {
        val targetState = targetState(args, backStackDepth)
        return updateTransition(targetState = targetState, label = "CustomDecoratorTransition")
    }

    @Composable
    override fun AnimatedContentScope.Decoration(
        targetState: CustomNavState<T>,
        innerContent: @Composable (T) -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxSize()) { innerContent(targetState.args.first()) }
    }
}

@Composable
@Preview
fun App(circuit: Circuit) {
    MaterialTheme {
        CircuitCompositionLocals(circuit) {
            val backStack = rememberSaveableBackStack(root = ConversationListScreen)
            val navigator = rememberCircuitNavigator(backStack, onRootPop = {})

            // NavigableCircuitContent appears not to pop the backstack
            // unless we delegate to the navigator from the BackHandler
            BackHandler {
                navigator.pop()
            }
            NavigableCircuitContent(
                navigator = navigator,
                backStack = backStack,
                decoratorFactory = GestureNavigationDecorationFactory {  },
            )
        }
    }
}