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
import org.jetbrains.compose.resources.stringResource
import solennechatapp.composeapp.generated.resources.Res
import solennechatapp.composeapp.generated.resources.back_button_description

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = stringResource(Res.string.back_button_description),
        modifier = modifier
            .size(24.dp)
            .clickable(onClick = onClick)
    )
}
