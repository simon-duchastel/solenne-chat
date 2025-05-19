package com.duchastel.simon.solenne.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.ui.icons.coffeeIcon
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BuyMeCoffeeFooter(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9CB3C))  // Yellow color matching the image
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = coffeeIcon(),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Like this app? Buy Me a Coffee",
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
internal fun BuyMeCoffeeFooter_Preview() {
    BuyMeCoffeeFooter(
        onClick = {}
    )
}