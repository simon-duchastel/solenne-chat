package com.duchastel.simon.solenne.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import solennechatapp.composeapp.generated.resources.Res
import solennechatapp.composeapp.generated.resources.buy_me_a_coffee_logo

@Composable
fun coffeeIcon(): Painter = painterResource(Res.drawable.buy_me_a_coffee_logo)