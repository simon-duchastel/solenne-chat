package com.duchastel.simon.solenne.ui.components

import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.ui.icons.githubIcon
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import solennechatapp.composeapp.generated.resources.Res
import solennechatapp.composeapp.generated.resources.github_repo_description
import solennechatapp.composeapp.generated.resources.view_source_button

@Composable
fun GithubSourceFooter(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(2.dp, color = Color.Black, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = githubIcon(),
            contentDescription = stringResource(Res.string.github_repo_description),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(Res.string.view_source_button),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
internal fun SourceFooter_Preview() {
    GithubSourceFooter(
        onClick = {}
    )
}
