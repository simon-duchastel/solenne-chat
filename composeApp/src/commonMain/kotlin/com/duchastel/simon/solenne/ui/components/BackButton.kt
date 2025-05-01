package com.duchastel.simon.solenne.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = "Back",
        modifier = modifier
            .size(48.dp)
            .clickable(onClick = onClick)
            .padding(12.dp)
    )
}