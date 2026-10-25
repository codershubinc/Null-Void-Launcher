package com.codershubinc.nullvoidlauncher.ui.github.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.model.GithubProfile

@Composable
fun GithubStatsGrid(
    profile: GithubProfile,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ProfileStatBadge("Repositories", profile.publicRepos.toString(), Icons.Rounded.Code)
        ProfileStatBadge("Followers", profile.followers.toString(), Icons.Rounded.Group)
        ProfileStatBadge("Following", profile.following.toString(), Icons.Rounded.PersonAdd)
    }
}

@Composable
fun ProfileStatBadge(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color = Color(0xFF3D5AFE)
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
