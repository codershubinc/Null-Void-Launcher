package com.codershubinc.nullvoidlauncher.ui.github

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.GithubProfile
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.utils.NetworkImage


@Composable
fun GithubProfileScreen(
    username: String,
    userManager: UserManager,
    onOpenSettings: () -> Unit,
    onClose: () -> Unit
) {
    var profile by remember { mutableStateOf<GithubProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Re-fetch the API whenever the username changes
    LaunchedEffect(username) {
        isLoading = true
        profile = userManager.fetchGithubProfile(username,false)
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(text = "[ ⚙ ]", color = Color.DarkGray, fontSize = 24.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.clickable { onOpenSettings() })
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "> USER_PROFILE", color = Color.DarkGray, fontSize = 18.sp, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Text(text = "FETCHING SECURE DATA...", color = Color.LightGray, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        } else if (profile != null) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Column (
                    modifier = Modifier.weight(1f)
                ) {
                ProfileLine("ID", profile!!.login)
                ProfileLine("NAME", profile!!.name)
            }
                Column() {
                    Text(text = "[AVATAR]", color = Color.Gray, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    NetworkImage(
                        url = profile!!.avatarUrl,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(shape = RoundedCornerShape(8.dp)),

                    )
                }
            }



            ProfileLine("BIO", profile!!.bio)
            ProfileLine("ORG", profile!!.company)
            ProfileLine("REPOS", profile!!.publicRepos.toString())
        } else {
            Text(text = "ERR: USER NOT FOUND", color = Color.Red, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "[TAP || SWIPE LEFT TO RETURN]",
            color = Color.DarkGray,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onClose() }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileLine(key: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = "[$key]", color = Color.Gray, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
        Text(text = value.ifEmpty { "NULL" }, color = Color.White, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
    }
}